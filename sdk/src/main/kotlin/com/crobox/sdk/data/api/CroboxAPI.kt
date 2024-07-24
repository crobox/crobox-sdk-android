package com.crobox.sdk.data.api

import com.crobox.sdk.domain.PromotionsResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

internal interface CroboxAPI {

    @Headers("Content-Type: application/x-www-form-urlencoded; charset=utf-8")
    @POST("/promotions")
    fun promotions(
        @QueryMap options: Map<String, String>,
        @Body impressions: RequestBody
    ): Call<PromotionsResponse?>?

    @GET("/socket.gif")
    fun event(@QueryMap options: Map<String, String>): Call<Void?>
}