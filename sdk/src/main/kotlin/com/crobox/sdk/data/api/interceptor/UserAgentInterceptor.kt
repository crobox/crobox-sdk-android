package com.crobox.sdk.data.api.interceptor

import android.os.Build
import com.crobox.sdk.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttp
import okhttp3.Response

/**
 * Sends the platform's own Android user agent, followed by the HTTP stack and the SDK version, for example
 * `Dalvik/2.1.0 (Linux; U; Android 14; SM-S918B Build/UP1A) okhttp/4.9.3 CroboxSDK/1.0.11`.
 *
 * The server derives the OS, OS version and device model from the Dalvik part. A bare `okhttp/x.y.z` or a
 * custom product string tells it nothing about the device.
 */
class UserAgentInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("User-Agent", userAgent)
            .build()
        return chain.proceed(request)
    }

    internal companion object {

        val userAgent: String by lazy {
            build(
                System.getProperty("http.agent"),
                Build.VERSION.RELEASE,
                Build.MODEL,
                Build.ID
            )
        }

        internal fun build(httpAgent: String?, release: String?, model: String?, buildId: String?): String {
            val platform = httpAgent?.takeIf { it.isNotBlank() }
                ?: "Dalvik/2.1.0 (Linux; U; Android ${release ?: ""}; ${model ?: ""} Build/${buildId ?: ""})"
            return sanitize("$platform okhttp/${OkHttp.VERSION} CroboxSDK/${BuildConfig.VERSION_NAME}")
        }

        /** OkHttp rejects header values with characters outside printable ASCII, and a device model can have them. */
        internal fun sanitize(value: String): String =
            value.map { if (it == '\t' || it in ' '..'~') it else '_' }.joinToString("")
    }
}
