package com.crobox.sdk.Data.API

import com.crobox.sdk.Domian.BaseResponse
import com.crobox.sdk.Domian.PromotionsResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CroboxAPI {
    @POST("/promotions")
    fun promotions(@Body requestBody: RequestBody): Call<PromotionsResponse?>?

    @POST("/socket.gif")
    fun socket(@Body requestBody: RequestBody): Call<BaseResponse?>?
}