package com.crobox.sdk.domain

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * A gradient set in a color setting with the Crobox App's gradient input, which stores it as a CSS
 * `linear-gradient(...)` or `radial-gradient(circle, ...)`. A plain color is a gradient of that one color, so that
 * both are drawn the same way.
 */
internal sealed class Gradient {

    /** The color stops, at least two, in order of their position */
    abstract val stops: List<GradientStop>

    /**
     * A gradient along a line through the center, at [angle] degrees as in CSS: 0 runs from the bottom up, 90 from
     * left to right, and the angle turns clockwise.
     */
    data class Linear(val angle: Float, override val stops: List<GradientStop>) : Gradient()

    /** A circular gradient from the center out to the farthest corner */
    data class Radial(override val stops: List<GradientStop>) : Gradient()

    /** Returns a shader that paints this gradient over an area of [width] by [height] pixels, or null when it is empty */
    fun toShader(width: Float, height: Float): Shader? {
        if (width <= 0f || height <= 0f) return null
        val colors = stops.map { it.color.toArgb() }.toIntArray()
        val positions = stops.map { it.position }.toFloatArray()
        return when (this) {
            is Linear -> {
                val (x0, y0, x1, y1) = linearGradientLine(angle, width, height)
                LinearGradient(x0, y0, x1, y1, colors, positions, Shader.TileMode.CLAMP)
            }

            is Radial ->
                RadialGradient(
                    width / 2f,
                    height / 2f,
                    hypot(width / 2f, height / 2f),
                    colors,
                    positions,
                    Shader.TileMode.CLAMP
                )
        }
    }

    /**
     * Returns a drawable that fills its bounds with this gradient, with rounded corners of [cornerRadius] pixels, to
     * use as a view's background.
     */
    fun toDrawable(cornerRadius: Float = 0f): Drawable = GradientBackground(this, cornerRadius)
}

/** A color at [position] along the gradient, from 0 at its start to 1 at its end */
internal data class GradientStop(val color: Color, val position: Float)

/**
 * The start and end of a CSS linear gradient's line, as `[x0, y0, x1, y1]`. The line runs through the center at the
 * angle, and is just long enough for the corners to get the first and last color.
 */
internal fun linearGradientLine(angle: Float, width: Float, height: Float): FloatArray {
    val radians = Math.toRadians(angle.toDouble())
    val dx = sin(radians).toFloat()
    val dy = -cos(radians).toFloat()
    val halfLength = (abs(width * dx) + abs(height * dy)) / 2f
    val cx = width / 2f
    val cy = height / 2f
    return floatArrayOf(cx - dx * halfLength, cy - dy * halfLength, cx + dx * halfLength, cy + dy * halfLength)
}

internal object GradientParser {

    /** Parses a color setting: a gradient, or a plain color as a gradient of that one color */
    fun parseBackground(value: String?): Gradient? {
        return parse(value) ?: ColorUtil.toAndroidColor(value)?.let { color ->
            Gradient.Linear(180f, listOf(GradientStop(color, 0f), GradientStop(color, 1f)))
        }
    }

    private val angle = Regex("""^(-?\d+(?:\.\d+)?)deg$""")
    private val percentage = Regex("""^(-?\d+(?:\.\d+)?)%$""")

    /**
     * Parses `linear-gradient(<angle>deg, <stops>)` or `radial-gradient(circle, <stops>)`, where each stop is a hex
     * or `rgba(...)` color with an optional percentage, or returns null for anything else, a plain color included.
     */
    fun parse(value: String?): Gradient? {
        val css = value?.trim() ?: return null
        val open = css.indexOf('(')
        if (open < 0 || !css.endsWith(")")) return null
        val arguments = splitArguments(css.substring(open + 1, css.length - 1))
        return when (css.substring(0, open).trim().lowercase()) {
            "linear-gradient" -> {
                val degrees = arguments.firstOrNull()?.let { angle.matchEntire(it)?.groupValues?.get(1)?.toFloat() }
                // Without an angle, CSS runs a linear gradient from top to bottom
                val stops = parseStops(if (degrees != null) arguments.drop(1) else arguments) ?: return null
                Gradient.Linear(degrees ?: 180f, stops)
            }

            "radial-gradient" -> {
                if (arguments.firstOrNull()?.lowercase() != "circle") return null
                Gradient.Radial(parseStops(arguments.drop(1)) ?: return null)
            }

            else -> null
        }
    }

    /** Splits on the commas between arguments, not the ones inside a color such as `rgba(0, 0, 0, 0.5)` */
    private fun splitArguments(arguments: String): List<String> {
        val result = mutableListOf<String>()
        var depth = 0
        var start = 0
        arguments.forEachIndexed { i, c ->
            when (c) {
                '(' -> depth++
                ')' -> depth--
                ',' -> if (depth == 0) {
                    result += arguments.substring(start, i).trim()
                    start = i + 1
                }
            }
        }
        result += arguments.substring(start).trim()
        return result
    }

    private fun parseStops(arguments: List<String>): List<GradientStop>? {
        if (arguments.size < 2) return null
        val colors = mutableListOf<Color>()
        val positions = mutableListOf<Float?>()
        for (argument in arguments) {
            val colorEnd = if (argument.contains('(')) argument.indexOf(')') + 1 else argument.indexOfFirst { it.isWhitespace() }
            val color = if (colorEnd <= 0) argument else argument.substring(0, colorEnd)
            val position = if (colorEnd <= 0) "" else argument.substring(colorEnd).trim()
            colors += ColorUtil.toAndroidColor(color) ?: return null
            positions += if (position.isEmpty()) null else {
                percentage.matchEntire(position)?.groupValues?.get(1)?.toFloat()?.div(100f) ?: return null
            }
        }
        return resolvePositions(positions).mapIndexed { i, position -> GradientStop(colors[i], position) }
    }

    /**
     * Fills in the positions as CSS does: the first stop defaults to the start and the last to the end, a stop before
     * an earlier one moves up to it, and stops without a position spread evenly between the ones around them.
     */
    private fun resolvePositions(positions: List<Float?>): List<Float> {
        val resolved = positions.toMutableList()
        if (resolved.first() == null) resolved[0] = 0f
        if (resolved.last() == null) resolved[resolved.lastIndex] = 1f
        var highest = resolved[0]!!
        for (i in resolved.indices) {
            resolved[i]?.let { if (it < highest) resolved[i] = highest else highest = it }
        }
        var i = 1
        while (i < resolved.size) {
            if (resolved[i] == null) {
                val before = i - 1
                val after = (i until resolved.size).first { resolved[it] != null }
                val from = resolved[before]!!
                val step = (resolved[after]!! - from) / (after - before)
                for (j in i until after) resolved[j] = from + step * (j - before)
                i = after
            }
            i++
        }
        return resolved.map { it!!.coerceIn(0f, 1f) }
    }
}

private class GradientBackground(private val gradient: Gradient, private val cornerRadius: Float) : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()

    override fun onBoundsChange(bounds: Rect) {
        rect.set(bounds)
        paint.shader = gradient.toShader(rect.width(), rect.height())?.also { shader ->
            shader.setLocalMatrix(Matrix().apply { setTranslate(rect.left, rect.top) })
        }
    }

    override fun draw(canvas: Canvas) {
        if (paint.shader == null) return
        if (cornerRadius > 0f) {
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        } else {
            canvas.drawRect(rect, paint)
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}
