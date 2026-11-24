package com.crobox.sdk.domain

import android.graphics.Color
import com.crobox.sdk.domain.ColorUtil.parseColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ColorUtilTest {

    private val white = (Color.WHITE)
    private val black = (Color.BLACK)
    private val red = (Color.RED)
    private val green = (Color.GREEN)
    private val blue = (Color.BLUE)

    @Test
    fun testOpaqueColors() {
        assertEquals(white, parseColor("#ffffff"))
        assertEquals(red, parseColor("#ff0000"))
        assertEquals(green, parseColor("#00ff00"))
        assertEquals(blue, parseColor(("#0000ff")))
        assertEquals(black, parseColor("#000000"))
    }

    @Test
    fun test_RGBA_OpaqueColors() {
        val opaque = 1.0f
        assertEquals(white, parseColor("rgba(255, 255, 255, $opaque)"))
        assertEquals(red, parseColor("rgba(255, 0, 0, $opaque)"))
        assertEquals(green, parseColor("rgba(0, 255, 0, $opaque)"))
        assertEquals(black, parseColor("rgba(0, 0, 0, $opaque)"))
    }

    @Test
    fun test_RGBA_TransparentColors() {
        val alpha = 0.5f
        val whiteTransparent = (Color.argb(alpha, 1f, 1f, 1f))
        val blackTransparent = (Color.argb(alpha, 0f, 0f, 0f))
        val redTransparent = (Color.argb(alpha, 1f, 0f, 0f))
        val greenTransparent = (Color.argb(alpha, 0f, 1f, 0f))
        val blueTransparent = (Color.argb(alpha, 0f, 0f, 1f))

        assertEquals(whiteTransparent, parseColor("rgba(255, 255, 255, $alpha)"))
        assertEquals(redTransparent, parseColor("rgba(255, 0, 0, $alpha)"))
        assertEquals(greenTransparent, parseColor("rgba(0, 255, 0, $alpha)"))
        assertEquals(blueTransparent, parseColor("rgba(0,0, 255, $alpha)"))
        assertEquals(blackTransparent, parseColor("rgba(0, 0, 0, $alpha)"))
    }

    @Test
    fun testHexAlphaIsLastAsInCss() {
        val halfRed = (Color.argb(0x80, 255, 0, 0))
        assertEquals(halfRed, parseColor("#ff000080"))
        assertEquals(halfRed, parseColor("#FF000080"))
        assertEquals(red, parseColor("#ff0000ff"))
        assertEquals((Color.argb(0, 0, 0, 255)), parseColor("#0000ff00"))
    }

    @Test
    fun testHexShorthands() {
        assertEquals(red, parseColor("#f00"))
        assertEquals(white, parseColor("#FFF"))
        assertEquals((Color.argb(0x88, 0, 0, 255)), parseColor("#00f8"))
    }

    @Test
    fun testFailure() {
        assertNull(parseColor(null))
        assertNull(parseColor("err"))
        assertNull(parseColor("#"))
        assertNull(parseColor("#00000"))
        assertNull(parseColor("#0000000"))
        assertNull(parseColor("#gggggg"))
        assertNull(parseColor("#ff00000000"))
        assertNull(parseColor("rgba(255, 255, 255)"))
        assertNull(parseColor("rgba(255, 255, 255, 0 ,0)"))
    }

}