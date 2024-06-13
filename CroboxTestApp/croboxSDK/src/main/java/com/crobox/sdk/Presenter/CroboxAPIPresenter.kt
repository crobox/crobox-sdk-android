package com.crobox.sdk.Presenter

import com.crobox.sdk.Data.API.CroboxAPI
import com.crobox.sdk.Data.API.CroboxAPIClient
import com.crobox.sdk.Data.Model.RequestQueryParams
import com.crobox.sdk.Domian.BaseResponse
import com.crobox.sdk.Domian.PromotionsResponse
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

    fun socket(socketView: SocketView, params: RequestQueryParams) {

        val json = gson.toJson(params)
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

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
}