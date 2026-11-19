package com.crobox.sdk.domain

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs

/**
 * Draws each gradient as a badge's background and compares pixels with what Chromium draws for the same CSS, which is
 * what the App's preview shows. The expected colors were read from Chromium's screenshots of these cases, and the iOS
 * SDK checks the same ones.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class GradientRenderingTest {

    private data class Sample(val x: Int, val y: Int, val color: String)

    private data class Case(val css: String, val width: Int, val height: Int, val samples: List<Sample>)

    @Test
    fun drawsWhatTheBrowserDraws() {
        val failures = mutableListOf<String>()
        for (case in cases) {
            val bitmap = Bitmap.createBitmap(case.width, case.height, Bitmap.Config.ARGB_8888)
            GradientParser.parse(case.css)!!.toDrawable().apply {
                setBounds(0, 0, case.width, case.height)
                draw(Canvas(bitmap))
            }
            for (sample in case.samples) {
                val actual = bitmap.getPixel(sample.x, sample.y)
                val expected = Color.parseColor(sample.color)
                val difference = maxOf(
                    abs(Color.alpha(actual) - Color.alpha(expected)),
                    abs(Color.red(actual) - Color.red(expected)),
                    abs(Color.green(actual) - Color.green(expected)),
                    abs(Color.blue(actual) - Color.blue(expected))
                )
                if (difference > TOLERANCE) {
                    failures += "${case.css} at (${sample.x}, ${sample.y}): " +
                        "expected ${sample.color}, got #${"%08x".format(actual)}"
                }
            }
        }
        assertTrue(failures.joinToString("\n"), failures.isEmpty())
    }

    private val cases = listOf(
            Case(
                "linear-gradient(87deg, #24d9f3 0%, #f40472 62%, #f74703 100%)",
                200, 100,
                listOf(
                    Sample(20, 10, "#4dafd9"),
                    Sample(100, 10, "#d02988"),
                    Sample(180, 10, "#f6351f"),
                    Sample(20, 50, "#49b2db"),
                    Sample(100, 50, "#cc2c8a"),
                    Sample(180, 50, "#f63422"),
                    Sample(20, 90, "#46b6dd"),
                    Sample(100, 90, "#c9308c"),
                    Sample(180, 90, "#f63225")
                )
            ),
            Case(
                "linear-gradient(45deg, #ff0000 0%, #0000ff 100%)",
                200, 60,
                listOf(
                    Sample(20, 6, "#b60048"),
                    Sample(100, 6, "#680097"),
                    Sample(180, 6, "#1900e5"),
                    Sample(20, 30, "#ce0031"),
                    Sample(100, 30, "#7f007f"),
                    Sample(180, 30, "#3100ce"),
                    Sample(20, 54, "#e50019"),
                    Sample(100, 54, "#970068"),
                    Sample(180, 54, "#4800b6")
                )
            ),
            Case(
                "linear-gradient(#ff0000, #00ff00, #0000ff 60%, #ffffff, #000000)",
                120, 100,
                listOf(
                    Sample(12, 10, "#a55900"),
                    Sample(60, 10, "#a55900"),
                    Sample(108, 10, "#a55900"),
                    Sample(12, 50, "#0050ae"),
                    Sample(60, 50, "#0050ae"),
                    Sample(108, 50, "#0050ae"),
                    Sample(12, 90, "#797979"),
                    Sample(60, 90, "#797979"),
                    Sample(108, 90, "#797979")
                )
            ),
            Case(
                "linear-gradient(135deg, #22d8ef 0%, #ff6396 50%, #fafe00 100%)",
                150, 60,
                listOf(
                    Sample(15, 6, "#51c0dd"),
                    Sample(75, 6, "#cf7daa"),
                    Sample(135, 6, "#fe9a61"),
                    Sample(15, 30, "#83a5c8"),
                    Sample(75, 30, "#ff6595"),
                    Sample(135, 30, "#fcbd3f"),
                    Sample(15, 54, "#b68ab4"),
                    Sample(75, 54, "#fe8873"),
                    Sample(135, 54, "#fbe11d")
                )
            ),
            Case(
                "radial-gradient(circle, #ffffff 0%, #000000 100%)",
                200, 100,
                listOf(
                    Sample(20, 10, "#343434"),
                    Sample(100, 10, "#a5a5a5"),
                    Sample(180, 10, "#323232"),
                    Sample(20, 50, "#494949"),
                    Sample(100, 50, "#fdfdfd"),
                    Sample(180, 50, "#474747"),
                    Sample(20, 90, "#333333"),
                    Sample(100, 90, "#a2a2a2"),
                    Sample(180, 90, "#313131")
                )
            )
    )

    companion object {
        /** Per color channel, out of 255: room for rounding and dithering, and too little to see */
        const val TOLERANCE = 3
    }
}
