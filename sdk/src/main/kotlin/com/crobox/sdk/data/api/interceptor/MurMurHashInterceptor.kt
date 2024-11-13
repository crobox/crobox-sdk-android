package com.crobox.sdk.data.api.interceptor

import com.crobox.sdk.utils.murmur.MurmurHash3_32
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class MurMurHashInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        // Retrieve the original request
        val originalRequest: Request = chain.request()

        // Get the original URL
        val originalUrl = originalRequest.url

        val queryParamsAsString = originalUrl.encodedQuery ?: ""

        val murmur = MurmurHash3_32.hash(queryParamsAsString)
//        println("For data $queryParamsAsString:  base36: $murmur")

        // Build a new request with an additional query parameter
        val url = originalRequest.url.newBuilder()
            .addQueryParameter("z", murmur) // Add your query parameter here
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(url)
            .build()

        // Proceed with the new request
        return chain.proceed(newRequest)
    }
}