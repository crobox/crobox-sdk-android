package com.crobox.sdk.Data.API

import com.crobox.sdk.Domain.Constant
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.util.logging.Level


internal object CroboxAPIClient {
    val clientWithOutToken: Retrofit
        get() {
            val headerLogging = HttpLoggingInterceptor()
            val bodyLogging = HttpLoggingInterceptor()

            headerLogging.setLevel(HttpLoggingInterceptor.Level.HEADERS)
            bodyLogging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(bodyLogging)
                .readTimeout(60 * 5, TimeUnit.SECONDS)
                .connectTimeout(60 * 5, TimeUnit.SECONDS)
                .addInterceptor(
                    Interceptor { chain: Interceptor.Chain ->
                        var request: Request.Builder = chain.request().newBuilder()
                        request = request.method(chain.request().method, chain.request().body)
                        chain.proceed(request.build())
                    }).build()

            val gson = GsonBuilder()
                .setLenient()
                .create()

            return Retrofit.Builder()
                .client(client)
                .baseUrl(Constant.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
}