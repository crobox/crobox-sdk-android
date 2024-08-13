package com.crobox.sdk.domain

import android.graphics.Color


internal object ColorUtil {

    fun toAndroidColor(fontColor: String?): Color? {
        try {
            if (fontColor?.startsWith("#") == true) {
                val fontColorInt: Int = Color.parseColor(fontColor)
                return Color.valueOf(fontColorInt)
            } else if (fontColor?.startsWith("rgba") == true) {
                val c: List<Float> =
                    fontColor
                        .removePrefix("rgba(")
                        .removeSuffix(")")
                        .split(",")
                        .map { it.toFloat() }
                if (c.size != 4) {
                    return null
                }
                val red = c.get(0).toInt()
                val green = c.get(1).toInt()
                val blue = c.get(2).toInt()
                val alpha = c.get(3) // 0.5
                val alphaInt = (alpha * 255.0f + 0.5f).toInt()
                return Color.valueOf(Color.argb(alphaInt, red, green, blue))
            } else {
                return null
            }
        } catch (_: Throwable) {
            return null
        }
    }
}