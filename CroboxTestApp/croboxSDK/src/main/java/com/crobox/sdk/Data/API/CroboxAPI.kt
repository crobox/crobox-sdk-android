package com.crobox.sdk.Data.API

import com.crobox.sdk.Domain.BaseResponse
import com.crobox.sdk.Domain.PromotionsResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CroboxAPI {
    @POST("/promotions")
    fun promotions(@Body requestBody: RequestBody): Call<PromotionsResponse?>?

    @POST("/socket.gif")
    fun socket(@Body requestBody: RequestBody): Call<BaseResponse?>?
}