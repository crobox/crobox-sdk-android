package com.crobox.sdk.Presenter

import com.crobox.sdk.Core.Crobox
import com.crobox.sdk.Data.API.CroboxAPI
import com.crobox.sdk.Data.API.CroboxAPIClient
import com.crobox.sdk.Data.Model.*
import com.crobox.sdk.Domain.BaseResponse
import com.crobox.sdk.Domain.PromotionsResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CroboxAPIPresenter {

    private val apiInterface: CroboxAPI = CroboxAPIClient.clientWithOutToken.create(
        CroboxAPI::class.java
    )
    private val gson = Gson()

    fun promotions(promotionView: PromotionView, params: RequestQueryParams) {

        val json = gson.toJson(params)
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        apiInterface.promotions(requestBody)
            ?.enqueue(object : Callback<PromotionsResponse?> {
                override fun onResponse(
                    call: Call<PromotionsResponse?>,
                    response: Response<PromotionsResponse?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            promotionView.onPromotions(response.body())
                        } else {
                            promotionView.onError(response.body().toString())
                        }
                    } catch (ex: Exception) {
                        promotionView.onError(ex.message ?: "")
                    }
                }

                override fun onFailure(call: Call<PromotionsResponse?>, t: Throwable) {
                    promotionView.onError(t.message ?: "")
                }
            })
    }

    fun socket(
        socketView: SocketView,
        eventType: EventType,
        queryParams: RequestQueryParams,
        additionalParams: Any?
    ) {
        val requestBody = createRequestBody(queryParams, additionalParams, eventType)

        apiInterface.socket(requestBody)
            ?.enqueue(object : Callback<BaseResponse?> {
                override fun onResponse(
                    call: Call<BaseResponse?>,
                    response: Response<BaseResponse?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                socketView.onSocketSuccess(it.data)
                            }
                        } else {
                            socketView.onError(response.body().toString())
                        }
                    } catch (ex: Exception) {
                        socketView.onError(ex.message ?: "")
                    }
                }

                override fun onFailure(call: Call<BaseResponse?>, t: Throwable) {
                    socketView.onError(t.message ?: "")
                }
            })
    }

    private fun createRequestBody(
        queryParams: RequestQueryParams,
        additionalParams: Any?,
        eventType: EventType
    ): RequestBody {
        val gson = Gson()

        // Mandatory parameters
        val parameters = mutableMapOf<String, Any>(
            "cid" to Crobox.instance.containerId,
            "e" to queryParams.viewCounter,
            "vid" to queryParams.viewId,
            "pid" to queryParams.visitorId,
            "t" to eventType
        )

        // Optional parameters
        queryParams.currencyCode?.let { parameters["cc"] = it }
        queryParams.localeCode?.let { parameters["lc"] = it }
        queryParams.userId?.let { parameters["uid"] = it }
        queryParams.timestamp?.let { parameters["ts"] = it }
        queryParams.timezone?.let { parameters["tz"] = it }
        queryParams.pageType?.let { parameters["pt"] = it }
        queryParams.pageUrl?.let { parameters["cp"] = it }
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
            EventType.AddCart -> (additionalParams as? AddCartQueryParams)?.let {
                addToCartEvent(
                    it,
                    parameters
                )
            }
            EventType.RemoveCart -> (additionalParams as? RemoveFromCartQueryParams)?.let {
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

        // Convert parameters map to JSON and create request body
        val mergedJson = gson.toJson(parameters)
        return mergedJson.toRequestBody("application/json".toMediaTypeOrNull())
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
        errorQueryParams.devicePixelRatio?.let { parameters["dpr"] = it }
        errorQueryParams.deviceLanguage?.let { parameters["ul"] = it }
        errorQueryParams.viewPortSize?.let { parameters["vp"] = it }
        errorQueryParams.screenResolutionSize?.let { parameters["sr"] = it }
    }

    /***

    The following arguments are applicable for click events( where t=click ). They are all optional

     */
    private fun clickEvent(clickParams: ClickQueryParams, parameters: MutableMap<String, Any>) {
        clickParams.productId?.let { parameters["pi"] = it }
        clickParams.category?.let { parameters["cat"] = it }
        clickParams.price?.let { parameters["price"] = it }
        clickParams.quantity?.let { parameters["qty"] = it }
    }

    /***

    The following arguments are applicable for AddToCart events( where t=cart ). They are all optional

     */
    private fun addToCartEvent(
        addCartQueryParams: AddCartQueryParams,
        parameters: MutableMap<String, Any>
    ) {
        addCartQueryParams.productId?.let { parameters["pi"] = it }
        addCartQueryParams.category?.let { parameters["cat"] = it }
        addCartQueryParams.price?.let { parameters["price"] = it }
        addCartQueryParams.quantity?.let { parameters["qty"] = it }
    }

    /***

    The following arguments are applicable for RemoveFromCart events( where t=rmcart ). They are all optional

     */
    private fun removeFromCartEvent(
        removeFromCartQueryParams: RemoveFromCartQueryParams,
        parameters: MutableMap<String, Any>
    ) {
        removeFromCartQueryParams.productId?.let { parameters["pi"] = it }
        removeFromCartQueryParams.category?.let { parameters["cat"] = it }
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