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
import com.crobox.sdk.data.model.PageViewParams
import com.crobox.sdk.data.model.ProductParams
import com.crobox.sdk.data.model.PurchaseParams
import com.crobox.sdk.data.model.RequestQueryParams
import com.crobox.sdk.domain.BaseResponse
import com.crobox.sdk.domain.PromotionsResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class CroboxAPIPresenter(private val config: CroboxConfig) {

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

    fun event(
        eventType: EventType, queryParams: RequestQueryParams, additionalParams: Any?
    ) {

        val parameters = eventQuery(queryParams, additionalParams, eventType)
        val stringParameters = parameters.mapValues { it.value.toString() }

        apiInterface.event(stringParameters).enqueue(object : Callback<BaseResponse?> {
            override fun onResponse(
                call: Call<BaseResponse?>, response: Response<BaseResponse?>
            ) {
                try {
                    if (!response.isSuccessful) {
                        CroboxDebug.eventError(response.body().toString())
                    }
                } catch (ex: Exception) {
                    CroboxDebug.eventError(response.body().toString())
                }
            }

            override fun onFailure(call: Call<BaseResponse?>, t: Throwable) {
                CroboxDebug.eventError(t.message.toString())
            }
        })
    }

    private fun eventQuery(
        queryParams: RequestQueryParams, additionalParams: Any?, eventType: EventType
    ): Map<String, Any> {

        // Mandatory parameters
        val parameters = commonQueryParams(queryParams)
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


    private fun promotionRequestBody(impressions: List<String>): RequestBody {
        val bodyStr =
            impressions.indices.zip(impressions).joinToString("&") { t -> "${t.first}=${t.second}" }

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
        queryParams: RequestQueryParams
    ): MutableMap<String, Any> {
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
        config.localeCode?.let { parameters["lc"] = it.toString() }
        config.userId?.let { parameters["uid"] = it }
        parameters["ts"] = CroboxEncoder.toBase36(System.currentTimeMillis())
        config.timezone?.let { parameters["tz"] = it }
        queryParams.pageType?.let { parameters["pt"] = it.value }
        queryParams.pageName?.let { parameters["lh"] = it }
        queryParams.customProperties?.let {
            for ((key, value) in it) {
                parameters["cp.$key"] = value
            }
        }

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
        checkoutParams.step?.let { parameters["stp"] = it }
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
        productParams.otherProductIds?.let { parameters["lst"] = it.joinToString() }
    }

    private fun impressions(
        impressions:Set<ProductParams>,
        parameters: MutableMap<String, Any>
    ) {
        val impressionsList = impressions.map{ it.productId }.toList()
        for (m in impressionsList.indices.zip(impressionsList)) {
            parameters["imp[${m.first}]"] = m.second
        }
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