package com.crobox.sdk.data.api

import com.crobox.sdk.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val reqBuilder = request.newBuilder()
            .header("User-Agent", "Android SDK: ${BuildConfig.VERSION_CODE}/${BuildConfig.VERSION_NAME}")
        return chain.proceed(reqBuilder.build())
    }

}
