package com.crobox.sdk.presenter

import com.crobox.sdk.common.CroboxDebug
import com.crobox.sdk.common.CroboxEncoder
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.data.api.CroboxAPI
import com.crobox.sdk.data.api.CroboxAPIClient
import com.crobox.sdk.data.model.CartParams
import com.crobox.sdk.data.model.CheckoutParams
import com.crobox.sdk.data.model.ClickParams
import com.crobox.sdk.data.model.CustomEventParams
import com.crobox.sdk.data.model.ErrorParams
import com.crobox.sdk.data.model.EventType
import com.crobox.sdk.data.model.ProductParams
import com.crobox.sdk.data.model.PurchaseParams
import com.crobox.sdk.data.model.ViewContext
import com.crobox.sdk.data.model.ViewParams
import com.crobox.sdk.data.model.ViewType
import com.crobox.sdk.domain.CroboxError
import com.crobox.sdk.domain.PromotionsResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.URLEncoder
import java.util.TimeZone

internal class CroboxAPIPresenter(@Volatile var config: CroboxConfig) {

    private val apiInterface: CroboxAPI = CroboxAPIClient.clientWithOutToken.create(
        CroboxAPI::class.java
    )

    fun promotions(
        placementId: Int,
        view: ViewContext,
        productIds: List<String>,
        callback: PromotionCallback
    ) {

        val parameters = promotionsQuery(placementId, view)
        val stringParameters = parameters.mapValues { it.value.toString() }
        val requestBody = promotionRequestBody(productIds)

        apiInterface.promotions(stringParameters, requestBody)
            ?.enqueue(object : Callback<PromotionsResponse?> {
                override fun onResponse(
                    call: Call<PromotionsResponse?>, response: Response<PromotionsResponse?>
                ) {
                    try {
                        // http [200...300)
                        if (response.isSuccessful) {
                            response.body()?.let { callback.onPromotions(it) }
                                ?: callback.onError(CroboxError.InvalidResponse("The response has no body"))
                        } else {
                            // http errors [300...600)
                            val error = CroboxError.HttpError(response.code())
                            callback.onError(error)
                            CroboxDebug.promotionError(error.message ?: "")
                            reportFailure("promotions", view, error.message ?: "")
                        }
                    } catch (ex: Exception) {
                        callback.onError(CroboxError.InvalidResponse(ex.message ?: ex.javaClass.simpleName, ex))
                        CroboxDebug.promotionError(ex.message ?: "")
                    }
                }

                override fun onFailure(call: Call<PromotionsResponse?>, t: Throwable) {
                    callback.onError(promotionsFailure(t))
                    CroboxDebug.promotionError(t.message ?: "")
                    // A cancelled request is not an error
                    if (!call.isCanceled) {
                        reportFailure("promotions", view, failureMessage(t))
                    }
                }
            })
    }

    /**
     * @param viewType Overrides [ViewContext.viewType] for this event only. The override is passed separately,
     * instead of through `view.copy(..)`, so the event keeps using the view's counter (`e`). A copy would
     * start again at 0, and the collector drops events whose (cid, pid, vid, e) it has seen before.
     */
    fun event(
        eventType: EventType,
        view: ViewContext,
        params: Any?,
        viewType: ViewType? = null
    ) {

        val parameters = eventQuery(view, params, eventType, viewType)
        val stringParameters = parameters.mapValues { it.value.toString() }

        apiInterface.event(stringParameters).enqueue(object : Callback<Void?> {
            override fun onResponse(
                call: Call<Void?>, response: Response<Void?>
            ) {
                if (!response.isSuccessful) {
                    failed("HTTP ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                // A cancelled request is not an error
                if (!call.isCanceled) {
                    failed(failureMessage(t))
                }
            }

            private fun failed(message: String) {
                CroboxDebug.eventError(message)
                // Reporting a failed error event would loop
                if (eventType != EventType.Error) {
                    reportFailure(eventType.requestName, view, message)
                }
            }
        })
    }

    /**
     * Sends an error event for a request that failed, so that failures on users' devices show up in Crobox.
     *
     * @param request The request that failed, for example `pageview` or `promotions`. Sent as the error's name.
     */
    private fun reportFailure(request: String, view: ViewContext, message: String) {
        event(EventType.Error, view, failureReport(request, message))
    }

    internal fun eventQuery(
        view: ViewContext,
        params: Any?,
        eventType: EventType,
        viewType: ViewType?
    ): Map<String, Any> {

        // Mandatory parameters
        val parameters = commonQueryParams(view, viewType)
        parameters["t"] = eventType.type

        // Additional parameters based on event type
        when (eventType) {
            EventType.Error -> (params as? ErrorParams)?.let {
                errorEvent(it, parameters)
            }

            EventType.Click -> (params as? ClickParams)?.let {
                clickEvent(it, parameters)
            }

            EventType.AddCart -> (params as? CartParams)?.let {
                addToCartEvent(it, parameters)
            }

            EventType.RemoveCart -> (params as? CartParams)?.let {
                removeFromCartEvent(it, parameters)
            }

            EventType.CustomEvent -> (params as? CustomEventParams)?.let {
                customEvent(it, parameters)
            }

            EventType.PageView -> (params as? ViewParams)?.let {
                viewEvent(it, parameters)
            }

            EventType.Checkout -> (params as? CheckoutParams)?.let {
                checkoutEvent(it, parameters)
            }

            EventType.Purchase -> (params as? PurchaseParams)?.let {
                purchaseEvent(it, parameters)
            }
        }

        return parameters
    }


    internal fun promotionRequestBody(productIds: List<String>): RequestBody {
        // The server parses the body as a query string, so an id containing &, =, + or % must be encoded
        val bodyStr = productIds.withIndex()
            .joinToString("&") { (index, productId) -> "$index=${URLEncoder.encode(productId, "UTF-8")}" }

        return bodyStr.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    internal fun promotionsQuery(
        placementId: Int, view: ViewContext
    ): Map<String, Any> {
        val parameters = commonQueryParams(view)
        parameters["vpid"] = placementId.toString()
        return parameters
    }

    private fun commonQueryParams(
        view: ViewContext,
        viewType: ViewType? = null
    ): MutableMap<String, Any> {
        val config = this.config

        // Mandatory parameters. Only with "sdk" does the API return the visitor and session ids in the promotions
        // response as standard UUIDs; without it they come in a 22-character short form the SDK can't read.
        val parameters = mutableMapOf<String, Any>(
            "cid" to config.containerId,
            "e" to view.viewCounter(),
            "vid" to view.viewId,
            "pid" to config.visitorId.toString(),
            "sdk" to "2"
        )

        // Optional parameters
        config.localeCode?.let { parameters["lc"] = it.code }
        config.userId?.let { parameters["uid"] = it }
        parameters["ts"] = CroboxEncoder.toBase36(System.currentTimeMillis())
        parameters["tz"] = timezoneOffsetHours(config.timezone ?: TimeZone.getDefault())
        parameters["pt"] = (viewType ?: view.viewType).value
        view.viewName?.let { parameters["lh"] = it }
        // Visitor-level properties first, so page-level properties with the same key win
        config.customProperties?.let { customProperties(it, parameters) }
        view.customProperties?.let { customProperties(it, parameters) }

        return parameters
    }

    /**
     * The following arguments are applicable for error events( where t=error ). They are all optional.
     */
    private fun errorEvent(
        errorParams: ErrorParams, parameters: MutableMap<String, Any>
    ) {
        errorParams.tag?.let { parameters["tg"] = it }
        errorParams.name?.let { parameters["nm"] = it }
        errorParams.message?.let { parameters["msg"] = it }
        errorParams.file?.let { parameters["f"] = it }
        errorParams.line?.let { parameters["l"] = it }
    }

    /**
     * The following arguments are applicable for click events( where t=click ). They are all optional
     *
     */
    private fun clickEvent(clickParams: ClickParams, parameters: MutableMap<String, Any>) {
        clickParams.productId?.let { parameters["pi"] = it }
        clickParams.quantity?.let { parameters["qty"] = it }
    }

    /**
     * The following arguments are applicable for AddToCart events( where t=cart ). They are all optional
     */
    private fun addToCartEvent(
        cartParams: CartParams, parameters: MutableMap<String, Any>
    ) {
        cartParams.productId?.let { parameters["pi"] = it }
        cartParams.quantity?.let { parameters["qty"] = it }
    }

    /**
     * The following arguments are applicable for RemoveFromCart events( where t=rmcart ). They are all optional
     */
    private fun removeFromCartEvent(
        cartParams: CartParams, parameters: MutableMap<String, Any>
    ) {
        cartParams.productId?.let { parameters["pi"] = it }
        cartParams.quantity?.let { parameters["qty"] = it }
    }

    /**
     * The following arguments are applicable for click events( where t=event ). They are all optional
     */
    private fun customEvent(
        customEventParams: CustomEventParams, parameters: MutableMap<String, Any>
    ) {
        customEventParams.name?.let { parameters["nm"] = it }
        customEventParams.promotionId?.let { parameters["promoId"] = it }
        customEventParams.productId?.let { parameters["pi"] = it }
        customEventParams.quantity?.let { parameters["qty"] = it }
    }

    /**
     * The following arguments are applicable for view events( where t=pageview )
     */
    private fun viewEvent(viewParams: ViewParams, parameters: MutableMap<String, Any>) {
        viewParams.title?.let { parameters["dt"] = it }
        viewParams.product?.let { productParams(it, parameters) }
        viewParams.searchTerms?.let { parameters["st"] = it }
        viewParams.impressions?.let { impressions(it, parameters) }
        viewParams.customProperties?.let { customProperties(it, parameters) }
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
        parameters["tid"] = purchaseParams.transactionId
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
        productParams.quantity?.let { parameters["qty"] = it }
        productParams.list?.let { parameters["lst"] = it }
    }

    /**
     * Sends products in the collector's array format, `imp[N].pi`, `imp[N].qty`, `imp[N].lst` and `imp[N].pos`, so
     * quantity, list and position reach the server. The flat format, `imp[N]=id`, carries only the id.
     */
    private fun impressions(
        impressions: Collection<ProductParams>,
        parameters: MutableMap<String, Any>
    ) {
        for ((index, product) in impressions.withIndex()) {
            parameters["imp[$index].pi"] = product.productId
            product.quantity?.let { parameters["imp[$index].qty"] = it }
            product.list?.let { parameters["imp[$index].lst"] = it }
            // 1-based, the server treats 0 as an unknown position
            parameters["imp[$index].pos"] = index + 1
        }
    }

    /**
     * The error for a promotions request that failed without an HTTP status. Retrofit reports a response it cannot
     * read this way too, not only a request that got no answer.
     */
    internal fun promotionsFailure(t: Throwable): CroboxError =
        if (t is IOException) CroboxError.RequestFailed(t) else CroboxError.InvalidResponse(failureMessage(t), t)

    internal fun failureReport(request: String, message: String) =
        ErrorParams(tag = "sdk", name = request, message = message)

    /**
     * The exception's class and message. The request URL is left out: it carries the userId and custom properties.
     */
    internal fun failureMessage(t: Throwable): String =
        listOfNotNull(t.javaClass.simpleName, t.message).joinToString(": ")

    /**
     * The name of the request in error events, the same as the iOS SDK sends. Checkout and purchase events are
     * sent as page views, so [EventType.type] does not tell them apart.
     */
    internal val EventType.requestName: String
        get() = when (this) {
            EventType.Click -> "click"
            EventType.AddCart -> "addcart"
            EventType.RemoveCart -> "rmcart"
            EventType.PageView -> "pageview"
            EventType.Checkout -> "checkout"
            EventType.Purchase -> "purchase"
            EventType.Error -> "error"
            EventType.CustomEvent -> "custom"
        }

    /**
     * The current offset of [timeZone] from UTC in hours, in the same form as the web snippet: positive east of UTC,
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