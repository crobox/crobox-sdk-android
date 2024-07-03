package com.crobox.sdk.presenter

import com.crobox.sdk.common.CroboxEncoder
import com.crobox.sdk.common.CroboxDebug
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.data.api.CroboxAPI
import com.crobox.sdk.data.api.CroboxAPIClient
import com.crobox.sdk.data.model.*
import com.crobox.sdk.domain.BaseResponse
import com.crobox.sdk.domain.PromotionsResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CroboxAPIPresenter(private val config: CroboxConfig) {

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
        val body = promotionRequestBody(impressions)

        apiInterface.promotions(stringParameters, body)
            ?.enqueue(object : Callback<PromotionsResponse?> {
                override fun onResponse(
                    call: Call<PromotionsResponse?>,
                    response: Response<PromotionsResponse?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            promotionCallback.onPromotions(response.body())
                            CroboxDebug.printText(response.body().toString())
                        } else {
                            promotionCallback.onError(response.body().toString())
                            CroboxDebug.promotionError(response.body().toString())
                        }
                    } catch (ex: Exception) {
                        promotionCallback.onError(ex.message ?: "")
                        CroboxDebug.promotionError(ex.message.toString())
                    }
                }

                override fun onFailure(call: Call<PromotionsResponse?>, t: Throwable) {
                    promotionCallback.onError(t.message ?: "")
                    CroboxDebug.promotionError(t.message.toString())
                }
            })
    }

    fun event(
        eventType: EventType,
        queryParams: RequestQueryParams,
        additionalParams: Any?
    ) {

        val parameters = eventQuery(queryParams, additionalParams, eventType)
        val stringParameters = parameters.mapValues { it.value.toString() }

        apiInterface.event(stringParameters)
            ?.enqueue(object : Callback<BaseResponse?> {
                override fun onResponse(
                    call: Call<BaseResponse?>,
                    response: Response<BaseResponse?>
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
        queryParams: RequestQueryParams,
        additionalParams: Any?,
        eventType: EventType
    ): Map<String, Any> {

        // Mandatory parameters
        val parameters = mutableMapOf<String, Any>(
            "cid" to config.containerId,
            "e" to queryParams.viewCounter(),
            "vid" to queryParams.viewId,
            "pid" to config.visitorId.toString(),
            "t" to eventType.type
        )

        // Optional parameters
        config.currencyCode?.let { parameters["cc"] = it }
        config.localeCode?.let { parameters["lc"] = it.toString() }
        config.userId?.let { parameters["uid"] = it }
        parameters["ts"] = CroboxEncoder.toBase36(System.currentTimeMillis())
        config.timezone?.let { parameters["tz"] = it }
        queryParams.pageType?.let { parameters["pt"] = it.value }
        queryParams.pageName?.let { parameters["cp"] = it }
        queryParams.customProperties?.let { parameters["lh"] = it }

        // Additional parameters based on event type
        when (eventType) {
            EventType.Error -> (additionalParams as? ErrorQueryParams)?.let {
                errorEvent(
                    it,
                    parameters
                )
            }

            EventType.Click -> (additionalParams as? ClickQueryParams)?.let {
                clickEvent(
                    it,
                    parameters
                )
            }

            EventType.AddCart -> (additionalParams as? CartQueryParams)?.let {
                addToCartEvent(
                    it,
                    parameters
                )
            }

            EventType.RemoveCart -> (additionalParams as? CartQueryParams)?.let {
                removeFromCartEvent(
                    it,
                    parameters
                )
            }

            EventType.CustomEvent -> (additionalParams as? CustomQueryParams)?.let {
                customEvent(
                    it,
                    parameters
                )
            }

            else -> {
                // do nothing
            }
        }

        CroboxDebug.printParams(parameters)

        return parameters
    }


    private fun promotionRequestBody(
        impressions: List<String>
    ): String {
        return impressions.indices.zip(impressions)
            .joinToString("&") { t -> "${t.first}=${t.second}" }
    }

    private fun promotionsQuery(
        placeholderId: Int,
        queryParams: RequestQueryParams
    ): Map<String, Any> {
        val gson = Gson()

        // Mandatory parameters
        val parameters = mutableMapOf<String, Any>(
            "cid" to config.containerId,
            "vpid" to placeholderId.toString(),
            "e" to queryParams.viewCounter(),
            "vid" to queryParams.viewId,
            "pid" to config.visitorId.toString()
        )

        // Optional parameters
        config.currencyCode?.let { parameters["cc"] = it }
        config.localeCode?.let { parameters["lc"] = it.toString() }
        config.userId?.let { parameters["uid"] = it }
        parameters["ts"] = CroboxEncoder.toBase36(System.currentTimeMillis())
        config.timezone?.let { parameters["tz"] = it }
        queryParams.pageType?.let { parameters["pt"] = it.value }
        queryParams.pageName?.let { parameters["cp"] = it }
        queryParams.customProperties?.let { parameters["lh"] = it }

        CroboxDebug.printParams(parameters)

        return parameters
    }

    /***
    The following arguments are applicable for error events( where t=error ). They are all optional.
     */
    private fun errorEvent(
        errorQueryParams: ErrorQueryParams,
        parameters: MutableMap<String, Any>
    ) {
        errorQueryParams.tag?.let { parameters["tg"] = it }
        errorQueryParams.name?.let { parameters["nm"] = it }
        errorQueryParams.message?.let { parameters["msg"] = it }
        errorQueryParams.file?.let { parameters["f"] = it }
        errorQueryParams.line?.let { parameters["l"] = it }
    }

    /***

    The following arguments are applicable for click events( where t=click ). They are all optional

     */
    private fun clickEvent(clickParams: ClickQueryParams, parameters: MutableMap<String, Any>) {
        clickParams.productId?.let { parameters["pi"] = it }
        clickParams.price?.let { parameters["price"] = it }
        clickParams.quantity?.let { parameters["qty"] = it }
    }

    /***

    The following arguments are applicable for AddToCart events( where t=cart ). They are all optional

     */
    private fun addToCartEvent(
        addCartQueryParams: CartQueryParams,
        parameters: MutableMap<String, Any>
    ) {
        addCartQueryParams.productId?.let { parameters["pi"] = it }
        addCartQueryParams.price?.let { parameters["price"] = it }
        addCartQueryParams.quantity?.let { parameters["qty"] = it }
    }

    /***

    The following arguments are applicable for RemoveFromCart events( where t=rmcart ). They are all optional

     */
    private fun removeFromCartEvent(
        removeFromCartQueryParams: CartQueryParams,
        parameters: MutableMap<String, Any>
    ) {
        removeFromCartQueryParams.productId?.let { parameters["pi"] = it }
        removeFromCartQueryParams.price?.let { parameters["price"] = it }
        removeFromCartQueryParams.quantity?.let { parameters["qty"] = it }
    }

    /***

    The following arguments are applicable for click events( where t=event ). They are all optional

     */
    private fun customEvent(
        customQueryParams: CustomQueryParams,
        parameters: MutableMap<String, Any>
    ) {
        customQueryParams.name?.let { parameters["nm"] = it }
        customQueryParams.promotionId?.let { parameters["promoId"] = it }
        customQueryParams.productId?.let { parameters["pi"] = it }
        customQueryParams.price?.let { parameters["price"] = it }
        customQueryParams.quantity?.let { parameters["qty"] = it }
    }
}