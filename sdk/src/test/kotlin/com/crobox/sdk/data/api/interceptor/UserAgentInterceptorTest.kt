package com.crobox.sdk.data.api.interceptor

import com.crobox.sdk.BuildConfig
import okhttp3.OkHttp
import org.junit.Assert.assertEquals
import org.junit.Test

class UserAgentInterceptorTest {

    private val suffix = "okhttp/${OkHttp.VERSION} CroboxSDK/${BuildConfig.VERSION_NAME}"

    @Test
    fun usesThePlatformUserAgent() {
        val ua = UserAgentInterceptor.build(
            "Dalvik/2.1.0 (Linux; U; Android 14; SM-S918B Build/UP1A)", "14", "SM-S918B", "UP1A"
        )
        assertEquals("Dalvik/2.1.0 (Linux; U; Android 14; SM-S918B Build/UP1A) $suffix", ua)
    }

    @Test
    fun buildsADalvikUserAgentWhenThePlatformHasNone() {
        val ua = UserAgentInterceptor.build(null, "14", "Pixel 8", "AP1A")
        assertEquals("Dalvik/2.1.0 (Linux; U; Android 14; Pixel 8 Build/AP1A) $suffix", ua)
    }

    @Test
    fun replacesCharactersOkHttpRejects() {
        assertEquals("Galaxy_ S", UserAgentInterceptor.sanitize("Galaxy™ S"))
    }
}
