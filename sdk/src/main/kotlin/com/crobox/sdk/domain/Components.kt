package com.crobox.sdk.domain

import android.graphics.Color
import android.graphics.Shader
import android.graphics.drawable.Drawable
import com.crobox.sdk.domain.ColorUtil.toAndroidColor

interface PromotionContentConfig {
    val name: String
}

data class ImageBadge(
    /// Image url
    val image: String,

    /// Alternate Text
    val altText: String?,

    /// Component name
    override val name: String
) : PromotionContentConfig

data class TextBadge(
    /// Text message
    val text: String,

    /// Font color as hex for opaque colors or rgba for transparent colors
    val fontColor: String?,

    /// Background color as hex for opaque colors or rgba for transparent colors, or a CSS linear-gradient(...) or
    /// radial-gradient(circle, ...) when it is set with the gradient input
    val backgroundColor: String?,

    /// Border color as hex for opaque colors or rgba for transparent colors
    val borderColor: String?,

    /// Component name
    override val name: String
) : PromotionContentConfig {
    fun fontColorAndroid(): Color? {
        return toAndroidColor(fontColor)
    }

    /**
     * Returns the background color, or null when there is none or the background is a gradient
     */
    fun backgroundColorAndroid(): Color? {
        return toAndroidColor(backgroundColor)
    }

    /**
     * Returns a drawable that fills its bounds with the background, a plain color or a gradient, with rounded corners
     * of [cornerRadius] pixels, to use as a view's background, or null when there is none
     */
    fun backgroundDrawable(cornerRadius: Float = 0f): Drawable? {
        return GradientParser.parseBackground(backgroundColor)?.toDrawable(cornerRadius)
    }

    /**
     * Returns a shader that paints the background, a plain color or a gradient, over an area of [width] by [height]
     * pixels, for example for a Compose `ShaderBrush`, or null when there is no background or the area is empty
     */
    fun backgroundShader(width: Float, height: Float): Shader? {
        return GradientParser.parseBackground(backgroundColor)?.toShader(width, height)
    }

    fun borderColorAndroid(): Color? {
        return toAndroidColor(borderColor)
    }

}

data class SecondaryMessaging(
    /// Text message
    val text: String,

    /// Font color as hex for opaque colors or rgba for transparent colors
    val fontColor: String?,

    /// Component name
    override val name: String
) : PromotionContentConfig {

    fun fontColorAndroid(): Color? {
        return toAndroidColor(fontColor)
    }
}