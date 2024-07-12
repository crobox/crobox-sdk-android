package com.crobox.sdk.data.api

import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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

            return Retrofit.Builder().client(client).baseUrl(Constant.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)).build()
        }

    private fun client(): OkHttpClient {
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .readTimeout(60 * 5, TimeUnit.SECONDS)
            .connectTimeout(60 * 5, TimeUnit.SECONDS).build()

        return client

    }
}