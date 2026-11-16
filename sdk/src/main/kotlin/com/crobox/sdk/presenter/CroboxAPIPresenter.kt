package com.crobox.sdk.presenter

import com.crobox.sdk.common.CroboxDebug
import com.crobox.sdk.common.CroboxEncoder
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.data.api.CroboxAPI
import com.crobox.sdk.data.api.CroboxAPIClient
import com.crobox.sdk.data.model.CartQueryParams
import com.crobox.sdk.data.model.CheckoutParams
import com.crobox.sdk.data.model.ClickQueryParams
import com.crobox.sdk.data.model.CustomQueryParams
import com.crobox.sdk.data.model.ErrorQueryParams
import com.crobox.sdk.data.model.EventType
import com.crobox.sdk.data.model.PageType
import com.crobox.sdk.data.model.PageViewParams
import com.crobox.sdk.data.model.ProductParams
import com.crobox.sdk.data.model.PurchaseParams
import com.crobox.sdk.data.model.RequestQueryParams
import com.crobox.sdk.domain.PromotionsResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.util.TimeZone

internal class CroboxAPIPresenter(@Volatile var config: CroboxConfig) {

    private val apiInterface: CroboxAPI = CroboxAPIClient.clientWithOutToken.create(
        CroboxAPI::class.java
    )

    fun promotions(
        placeholderId: Int,
        queryParams: RequestQueryParams,
        impressions: List<String>,
        promotionCallback: PromotionCallback
    ) {

        val parameters = promotionsQuery(placeholderId, queryParams)
        val stringParameters = parameters.mapValues { it.value.toString() }
        val requestBody = promotionRequestBody(impressions)

        apiInterface.promotions(stringParameters, requestBody)
            ?.enqueue(object : Callback<PromotionsResponse?> {
                override fun onResponse(
                    call: Call<PromotionsResponse?>, response: Response<PromotionsResponse?>
                ) {
                    try {
                        // http [200...300)
                        if (response.isSuccessful) {
                            response.body()?.let { promotionCallback.onPromotions(it) }
                                ?: promotionCallback.onError("Successful http but null body")
                        } else {
                            // http errors [300...600)
                            promotionCallback.onError(response.body().toString())
                            CroboxDebug.promotionError(response.body().toString())
                        }
                    } catch (ex: Exception) {
                        promotionCallback.onError(ex.message ?: "")
                        CroboxDebug.promotionError(ex.message ?: "")
                    }
                }

                override fun onFailure(call: Call<PromotionsResponse?>, t: Throwable) {
                    promotionCallback.onError(t.message ?: "")
                    CroboxDebug.promotionError(t.message ?: "")
                }
            })
    }

    /**
     * @param pageType Overrides [RequestQueryParams.pageType] for this event only. The override is passed separately,
     * instead of through `queryParams.copy(..)`, so the event keeps using the view's counter (`e`). A copy would
     * start again at 0, and the collector drops events whose (cid, pid, vid, e) it has seen before.
     */
    fun event(
        eventType: EventType,
        queryParams: RequestQueryParams,
        additionalParams: Any?,
        pageType: PageType? = null
    ) {

        val parameters = eventQuery(queryParams, additionalParams, eventType, pageType)
        val stringParameters = parameters.mapValues { it.value.toString() }

        apiInterface.event(stringParameters).enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>, response: Response<Void?>
            ) {
                try {
                    if (!response.isSuccessful) {
                        CroboxDebug.eventError(response.body().toString())
                    }
                } catch (ex: Exception) {
                    CroboxDebug.eventError(response.body().toString())
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                CroboxDebug.eventError(t.message.toString())
            }
        })
    }

    internal fun eventQuery(
        queryParams: RequestQueryParams,
        additionalParams: Any?,
        eventType: EventType,
        pageType: PageType?
    ): Map<String, Any> {

        // Mandatory parameters
        val parameters = commonQueryParams(queryParams, pageType)
        parameters["t"] = eventType.type

        // Additional parameters based on event type
        when (eventType) {
            EventType.Error -> (additionalParams as? ErrorQueryParams)?.let {
                errorEvent(it, parameters)
            }

            EventType.Click -> (additionalParams as? ClickQueryParams)?.let {
                clickEvent(it, parameters)
            }

            EventType.AddCart -> (additionalParams as? CartQueryParams)?.let {
                addToCartEvent(it, parameters)
            }

            EventType.RemoveCart -> (additionalParams as? CartQueryParams)?.let {
                removeFromCartEvent(it, parameters)
            }

            EventType.CustomEvent -> (additionalParams as? CustomQueryParams)?.let {
                customEvent(it, parameters)
            }

            EventType.PageView -> (additionalParams as? PageViewParams)?.let {
                pageViewEvent(it, parameters)
            }

            EventType.Checkout -> (additionalParams as? CheckoutParams)?.let {
                checkoutEvent(it, parameters)
            }

            EventType.Purchase -> (additionalParams as? PurchaseParams)?.let {
                purchaseEvent(it, parameters)
            }
        }

        return parameters
    }


    internal fun promotionRequestBody(impressions: List<String>): RequestBody {
        // The server parses the body as a query string, so an id containing &, =, + or % must be encoded
        val bodyStr = impressions.withIndex()
            .joinToString("&") { (index, productId) -> "$index=${URLEncoder.encode(productId, "UTF-8")}" }

        return bodyStr.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun promotionsQuery(
        placeholderId: Int, queryParams: RequestQueryParams
    ): Map<String, Any> {
        val parameters = commonQueryParams(queryParams)
        parameters["vpid"] = placeholderId.toString()
        return parameters
    }

    private fun commonQueryParams(
        queryParams: RequestQueryParams,
        pageType: PageType? = null
    ): MutableMap<String, Any> {
        val config = this.config

        // Mandatory parameters
        val parameters = mutableMapOf<String, Any>(
            "cid" to config.containerId,
            "e" to queryParams.viewCounter(),
            "vid" to queryParams.viewId,
            "pid" to config.visitorId.toString(),
            "sdk" to "2"
        )

        // Optional parameters
        config.currencyCode?.let { parameters["cc"] = it.toString() }
        config.localeCode?.let { parameters["lc"] = it.code }
        config.userId?.let { parameters["uid"] = it }
        parameters["ts"] = CroboxEncoder.toBase36(System.currentTimeMillis())
        parameters["tz"] = config.timezone ?: timezoneOffsetHours()
        parameters["pt"] = (pageType ?: queryParams.pageType).value
        queryParams.pageName?.let { parameters["lh"] = it }
        // Visitor-level properties first, so page-level properties with the same key win
        config.customProperties?.let { customProperties(it, parameters) }
        queryParams.customProperties?.let { customProperties(it, parameters) }

        return parameters
    }

    /**
     * The following arguments are applicable for error events( where t=error ). They are all optional.
     */
    private fun errorEvent(
        errorQueryParams: ErrorQueryParams, parameters: MutableMap<String, Any>
    ) {
        errorQueryParams.tag?.let { parameters["tg"] = it }
        errorQueryParams.name?.let { parameters["nm"] = it }
        errorQueryParams.message?.let { parameters["msg"] = it }
        errorQueryParams.file?.let { parameters["f"] = it }
        errorQueryParams.line?.let { parameters["l"] = it }
    }

    /**
     * The following arguments are applicable for click events( where t=click ). They are all optional
     *
     */
    private fun clickEvent(clickParams: ClickQueryParams, parameters: MutableMap<String, Any>) {
        clickParams.productId?.let { parameters["pi"] = it }
        clickParams.price?.let { parameters["price"] = it }
        clickParams.quantity?.let { parameters["qty"] = it }
    }

    /**
     * The following arguments are applicable for AddToCart events( where t=cart ). They are all optional
     */
    private fun addToCartEvent(
        addCartQueryParams: CartQueryParams, parameters: MutableMap<String, Any>
    ) {
        addCartQueryParams.productId?.let { parameters["pi"] = it }
        addCartQueryParams.price?.let { parameters["price"] = it }
        addCartQueryParams.quantity?.let { parameters["qty"] = it }
    }

    /**
     * The following arguments are applicable for RemoveFromCart events( where t=rmcart ). They are all optional
     */
    private fun removeFromCartEvent(
        removeFromCartQueryParams: CartQueryParams, parameters: MutableMap<String, Any>
    ) {
        removeFromCartQueryParams.productId?.let { parameters["pi"] = it }
        removeFromCartQueryParams.price?.let { parameters["price"] = it }
        removeFromCartQueryParams.quantity?.let { parameters["qty"] = it }
    }

    /**
     * The following arguments are applicable for click events( where t=event ). They are all optional
     */
    private fun customEvent(
        customQueryParams: CustomQueryParams, parameters: MutableMap<String, Any>
    ) {
        customQueryParams.name?.let { parameters["nm"] = it }
        customQueryParams.promotionId?.let { parameters["promoId"] = it }
        customQueryParams.productId?.let { parameters["pi"] = it }
        customQueryParams.price?.let { parameters["price"] = it }
        customQueryParams.quantity?.let { parameters["qty"] = it }
    }

    /**
     * The following arguments are applicable for pageView events
     */
    private fun pageViewEvent(pageViewParams: PageViewParams, parameters: MutableMap<String, Any>) {
        pageViewParams.pageTitle?.let { parameters["dt"] = it }
        pageViewParams.product?.let { productParams(it, parameters) }
        pageViewParams.searchTerms?.let { parameters["st"] = it }
        pageViewParams.impressions?.let { impressions(it, parameters) }
        pageViewParams.customProperties?.let { customProperties(it, parameters) }
    }

    /**
     * The following arguments are applicable for checkout events
     */
    private fun checkoutEvent(checkoutParams: CheckoutParams, parameters: MutableMap<String, Any>) {
        checkoutParams.products?.let { impressions(it, parameters) }
        // The server only records a checkout when `stp` is present, so send 0 (unknown step) when none is given
        parameters["stp"] = checkoutParams.step ?: "0"
        checkoutParams.customProperties?.let { customProperties(it, parameters) }
    }

    /**
     * The following arguments are applicable for purchase events
     */
    private fun purchaseEvent(purchaseParams: PurchaseParams, parameters: MutableMap<String, Any>) {
        purchaseParams.products?.let { impressions(it, parameters) }
        purchaseParams.transactionId?.let { parameters["tid"] = it }
        purchaseParams.affiliation?.let { parameters["aff"] = it }
        purchaseParams.coupon?.let { parameters["cpn"] = it }
        purchaseParams.revenue?.let { parameters["rev"] = it }
        purchaseParams.customProperties?.let { customProperties(it, parameters) }
    }

    private fun productParams(
        productParams: ProductParams,
        parameters: MutableMap<String, Any>
    ) {
        parameters["pi"] = productParams.productId
        productParams.price?.let { parameters["price"] = it }
        productParams.quantity?.let { parameters["qty"] = it }
        // otherProductIds is not sent: the server reads `lst` as the name of a product list, not as product ids
    }

    /**
     * Sends products in the collector's array format, `imp[N].pi`, `imp[N].qty` and `imp[N].pos`, so quantity and
     * position reach the server. The flat format, `imp[N]=id`, carries only the id. Price is not sent, the server
     * takes it from the catalog.
     */
    private fun impressions(
        impressions: Set<ProductParams>,
        parameters: MutableMap<String, Any>
    ) {
        for ((index, product) in impressions.withIndex()) {
            parameters["imp[$index].pi"] = product.productId
            product.quantity?.let { parameters["imp[$index].qty"] = it }
            // 1-based, the server treats 0 as an unknown position
            parameters["imp[$index].pos"] = index + 1
        }
    }

    /**
     * The device's current offset from UTC in hours, in the same form as the web snippet: positive east of UTC,
     * with a fraction for zones such as India (5.5).
     */
    internal fun timezoneOffsetHours(
        timeZone: TimeZone = TimeZone.getDefault(),
        now: Long = System.currentTimeMillis()
    ): Number {
        val hours = timeZone.getOffset(now) / 3_600_000.0
        return if (hours % 1.0 == 0.0) hours.toInt() else hours
    }

    private fun customProperties(
        customProperties: Map<String, String>,
        parameters: MutableMap<String, Any>
    ) {
        for ((key, value) in customProperties) {
            parameters["cp.$key"] = value
        }
    }

}