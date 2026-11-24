package com.crobox.sdk.domain

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.RadialGradient
import com.crobox.sdk.domain.GradientParser.parse
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GradientTest {

    private fun color(hex: String): Int = ColorUtil.parseColor(hex)!!

    @Test
    fun parsesTheAppsLinearGradient() {
        assertEquals(
            Gradient.Linear(
                90f,
                listOf(
                    GradientStop(color("#22d8ef"), 0f),
                    GradientStop(color("#f4174d"), 0.7f),
                    GradientStop(color("#f7460d"), 1f)
                )
            ),
            parse("linear-gradient(90deg, #22d8ef 0%, #f4174d 70%, #f7460d 100%)")
        )
    }

    @Test
    fun parsesTheAppsRadialGradient() {
        assertEquals(
            Gradient.Radial(listOf(GradientStop(color("#ffffff"), 0f), GradientStop(color("#000000"), 1f))),
            parse("radial-gradient(circle, #ffffff 0%, #000000 100%)")
        )
    }

    @Test
    fun parsesTransparentStops() {
        val gradient = parse("linear-gradient(45deg, rgba(255, 0, 0, 0.5) 10%, #0000ff 90%)")
        assertEquals(
            Gradient.Linear(
                45f,
                listOf(
                    GradientStop(Color.argb(128, 255, 0, 0), 0.1f),
                    GradientStop(color("#0000ff"), 0.9f)
                )
            ),
            gradient
        )
    }

    @Test
    fun fillsInMissingPositionsAsCssDoes() {
        val gradient = parse("linear-gradient(#ff0000, #00ff00, #0000ff 60%, #ffffff, #000000)")!!
        assertEquals(180f, (gradient as Gradient.Linear).angle)
        assertArrayEquals(
            floatArrayOf(0f, 0.3f, 0.6f, 0.8f, 1f),
            gradient.stops.map { it.position }.toFloatArray(),
            0.0001f
        )
    }

    @Test
    fun movesAStopBeforeAnEarlierOneUpToIt() {
        val gradient = parse("linear-gradient(90deg, #ff0000 50%, #0000ff 20%)")!!
        assertEquals(listOf(0.5f, 0.5f), gradient.stops.map { it.position })
    }

    @Test
    fun isNullForAPlainColorOrSomethingElse() {
        assertNull(parse(null))
        assertNull(parse(""))
        assertNull(parse("#ff0000"))
        assertNull(parse("rgba(255, 0, 0, 0.5)"))
        assertNull(parse("linear-gradient(90deg, #ff0000 0%)"))
        assertNull(parse("linear-gradient(90deg, nonsense 0%, #ff0000 100%)"))
        assertNull(parse("linear-gradient(to right, #ff0000 0%, #0000ff 100%)"))
        assertNull(parse("radial-gradient(ellipse, #ff0000 0%, #0000ff 100%)"))
        assertNull(parse("conic-gradient(#ff0000, #0000ff)"))
    }

    @Test
    fun runsTheLineFromEdgeToEdgeForTheSides() {
        assertArrayEquals(floatArrayOf(0f, 50f, 200f, 50f), linearGradientLine(90f, 200f, 100f), 0.001f)
        assertArrayEquals(floatArrayOf(100f, 0f, 100f, 100f), linearGradientLine(180f, 200f, 100f), 0.001f)
        assertArrayEquals(floatArrayOf(100f, 100f, 100f, 0f), linearGradientLine(0f, 200f, 100f), 0.001f)
    }

    @Test
    fun runsTheLineThroughTheCornersPerpendicularToIt() {
        // At 45deg on a square, the line runs corner to corner
        assertArrayEquals(floatArrayOf(0f, 100f, 100f, 0f), linearGradientLine(45f, 100f, 100f), 0.001f)
    }

    @Test
    fun makesAShaderForTheArea() {
        val linear = parse("linear-gradient(90deg, #ff0000 0%, #0000ff 100%)")!!
        assertTrue(linear.toShader(100f, 20f) is LinearGradient)
        assertNull(linear.toShader(0f, 20f))
        val radial = parse("radial-gradient(circle, #ff0000 0%, #0000ff 100%)")!!
        assertTrue(radial.toShader(100f, 20f) is RadialGradient)
    }

    @Test
    fun parsesABackgroundThatIsAGradientOrAPlainColor() {
        assertTrue(GradientParser.parseBackground("linear-gradient(90deg, #ff0000 0%, #0000ff 100%)") is Gradient.Linear)
        assertEquals(
            Gradient.Linear(180f, listOf(GradientStop(color("#ff0000"), 0f), GradientStop(color("#ff0000"), 1f))),
            GradientParser.parseBackground("#ff0000")
        )
        assertNull(GradientParser.parseBackground(null))
        assertNull(GradientParser.parseBackground("nonsense"))
    }

    @Test
    fun textBadgeHasABackgroundForAGradientOrAColor() {
        fun badge(background: String?) = TextBadge("Sale", null, background, null)

        val gradient = badge("linear-gradient(90deg, #ff0000 0%, #0000ff 100%)")
        assertNotNull(gradient.backgroundDrawable())
        assertTrue(gradient.backgroundShader(100f, 20f) is LinearGradient)

        val solid = badge("#ff0000")
        assertNotNull(solid.backgroundDrawable())

        assertNull(badge(null).backgroundDrawable())
        assertNull(badge(null).backgroundShader(100f, 20f))
    }
}
