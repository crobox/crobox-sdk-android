package com.crobox.sdk.domain

import android.graphics.Color


internal object ColorUtil {

    /**
     * Parses a CSS color: `#RGB`, `#RGBA`, `#RRGGBB`, `#RRGGBBAA` or `rgba(r, g, b, a)`, or returns null when the
     * string is not a valid color.
     *
     * Hex colors are read in CSS order, with the alpha last. [Color.parseColor] reads 8 digits as `#AARRGGBB`, which
     * would turn a half-transparent red, `#ff000080`, into an opaque dark blue.
     */
    fun toAndroidColor(colorStr: String?): Color? {
        try {
            if (colorStr?.startsWith("#") == true) {
                return parseHexColor(colorStr)?.let { Color.valueOf(it) }
            } else if (colorStr?.startsWith("rgba") == true) {
                val rgba =
                    colorStr
                        .removePrefix("rgba(")
                        .removeSuffix(")")
                        .split(",")
                        .map { it.toFloat() }

                if (rgba.size != 4) {
                    throw IllegalArgumentException("Malformed input $colorStr")
                }
                val red = rgba[0].toInt()
                val green = rgba[1].toInt()
                val blue = rgba[2].toInt()
                val alpha = (rgba[3] * 255.0f + 0.5f).toInt()
                return Color.valueOf(Color.argb(alpha, red, green, blue))
            } else {
                throw IllegalArgumentException("Malformed input $colorStr")
            }
        } catch (_: Throwable) {
            return null
        }
    }

    /**
     * Parses `#RGB`, `#RGBA`, `#RRGGBB` or `#RRGGBBAA` into a color int, or returns null when it is not a valid hex
     * color.
     */
    internal fun parseHexColor(colorStr: String): Int? {
        var hex = colorStr.trim().removePrefix("#")
        if (!hex.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }) {
            return null
        }
        // Expand the shorthands #RGB and #RGBA to #RRGGBB and #RRGGBBAA
        if (hex.length == 3 || hex.length == 4) {
            hex = hex.map { "$it$it" }.joinToString("")
        }
        // No alpha means fully opaque
        if (hex.length == 6) {
            hex += "ff"
        }
        if (hex.length != 8) {
            return null
        }
        val value = hex.toLong(16)
        return Color.argb(
            (value and 0xFF).toInt(),
            ((value shr 24) and 0xFF).toInt(),
            ((value shr 16) and 0xFF).toInt(),
            ((value shr 8) and 0xFF).toInt()
        )
    }
}
