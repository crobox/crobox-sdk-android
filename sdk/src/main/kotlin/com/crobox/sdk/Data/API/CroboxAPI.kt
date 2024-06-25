package com.crobox.sdk.Data.API

import com.crobox.sdk.Domain.BaseResponse
import com.crobox.sdk.Domain.PromotionsResponse
import retrofit2.Call
import retrofit2.http.*

interface CroboxAPI {
    @POST("/promotions")
    fun promotions(@QueryMap options: Map<String, String>): Call<PromotionsResponse?>?

    @GET("/socket.gif")
    fun socket(@QueryMap options: Map<String, String>): Call<BaseResponse?>
}