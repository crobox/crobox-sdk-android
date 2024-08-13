package com.crobox.sdk.domain

import android.graphics.Color
import com.crobox.sdk.domain.ColorUtil.toAndroidColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ColorUtilTest {

    private val white = Color.valueOf(Color.WHITE)
    private val black = Color.valueOf(Color.BLACK)
    private val red = Color.valueOf(Color.RED)
    private val green = Color.valueOf(Color.GREEN)
    private val blue = Color.valueOf(Color.BLUE)

    @Test
    fun testOpaqueColors() {
        assertEquals(white, toAndroidColor("#ffffff"))
        assertEquals(red, toAndroidColor("#ff0000"))
        assertEquals(green, toAndroidColor("#00ff00"))
        assertEquals(blue, toAndroidColor(("#0000ff")))
        assertEquals(black, toAndroidColor("#000000"))
    }

    @Test
    fun test_RGBA_OpaqueColors() {
        val opaque = 1.0f
        assertEquals(white, toAndroidColor("rgba(255, 255, 255, $opaque)"))
        assertEquals(red, toAndroidColor("rgba(255, 0, 0, $opaque)"))
        assertEquals(green, toAndroidColor("rgba(0, 255, 0, $opaque)"))
        assertEquals(black, toAndroidColor("rgba(0, 0, 0, $opaque)"))
    }

    @Test
    fun test_RGBA_TransparentColors() {
        val alpha = 0.5f
        val whiteTransparent = Color.valueOf(Color.argb(alpha, 1f, 1f, 1f))
        val blackTransparent = Color.valueOf(Color.argb(alpha, 0f, 0f, 0f))
        val redTransparent = Color.valueOf(Color.argb(alpha, 1f, 0f, 0f))
        val greenTransparent = Color.valueOf(Color.argb(alpha, 0f, 1f, 0f))
        val blueTransparent = Color.valueOf(Color.argb(alpha, 0f, 0f, 1f))

        assertEquals(whiteTransparent, toAndroidColor("rgba(255, 255, 255, $alpha)"))
        assertEquals(redTransparent, toAndroidColor("rgba(255, 0, 0, $alpha)"))
        assertEquals(greenTransparent, toAndroidColor("rgba(0, 255, 0, $alpha)"))
        assertEquals(blueTransparent, toAndroidColor("rgba(0,0, 255, $alpha)"))
        assertEquals(blackTransparent, toAndroidColor("rgba(0, 0, 0, $alpha)"))
    }

    @Test
    fun testFailure() {
        assertNull(toAndroidColor(null))
        assertNull(toAndroidColor("err"))
        assertNull(toAndroidColor("#00000"))
        assertNull(toAndroidColor("#0000000"))
        assertNull(toAndroidColor("rgba(255, 255, 255)"))
        assertNull(toAndroidColor("rgba(255, 255, 255, 0 ,0)"))
    }

}