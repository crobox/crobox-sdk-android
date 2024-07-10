package com.crobox.sdk.data.api

import com.crobox.sdk.domain.BaseResponse
import com.crobox.sdk.domain.PromotionsResponse
import retrofit2.Call
import retrofit2.http.*

interface CroboxAPI {

    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @POST("/promotions")
    fun promotions(@QueryMap options: Map<String, String>, @Body impressions: String): Call<PromotionsResponse?>?

    @GET("/socket.gif")
    fun event(@QueryMap options: Map<String, String>): Call<BaseResponse?>
}