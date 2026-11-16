package com.crobox.sdk.data.api

import com.crobox.sdk.data.api.interceptor.MurMurHashInterceptor
import com.crobox.sdk.data.api.interceptor.UserAgentInterceptor
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit

internal object CroboxAPIClient {
    val clientWithOutToken: Retrofit
        get() {
            val client: OkHttpClient = client()

            val gson =
                GsonBuilder()
                    .setStrictness(Strictness.LENIENT)
                    .disableHtmlEscaping()
                    .enableComplexMapKeySerialization()
                    .registerTypeAdapter(UUID::class.java, CustomUUIDDeserializer())
                    .create()

            return Retrofit.Builder()
                .client(client).baseUrl(Constant.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)).build()
        }

    private fun client(): OkHttpClient {
        val client: OkHttpClient = OkHttpClient.Builder()
            // Promotions are rendered while the user waits, so fail fast instead of holding the screen for minutes
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(UserAgentInterceptor())
            .addInterceptor(MurMurHashInterceptor())
            .build()

        return client

    }
}