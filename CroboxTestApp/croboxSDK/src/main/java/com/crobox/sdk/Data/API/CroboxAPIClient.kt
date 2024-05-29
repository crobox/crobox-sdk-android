package com.crobox.sdk.Data.API

import com.crobox.sdk.Domian.Constant
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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

            return Retrofit.Builder()
                .client(client)
                .baseUrl(Constant.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
}