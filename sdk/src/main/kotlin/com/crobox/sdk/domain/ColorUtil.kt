package com.crobox.sdk.domain

import android.graphics.Color


internal object ColorUtil {

    fun toAndroidColor(colorStr: String?): Color? {
        try {
            if (colorStr?.startsWith("#") == true) {
                return Color.valueOf(Color.parseColor(colorStr))
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
}