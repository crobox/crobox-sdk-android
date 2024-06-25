package com.crobox.sdk.data.api

import com.crobox.sdk.domain.BaseResponse
import com.crobox.sdk.domain.PromotionsResponse
import retrofit2.Call
import retrofit2.http.*

interface CroboxAPI {
    @POST("/promotions")
    fun promotions(@QueryMap options: Map<String, String>): Call<PromotionsResponse?>?

    @GET("/socket.gif")
    fun socket(@QueryMap options: Map<String, String>): Call<BaseResponse?>
}