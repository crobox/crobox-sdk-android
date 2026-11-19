package com.crobox.sdk.domain

import android.graphics.Shader
import android.graphics.drawable.Drawable

/**
 * What a promotion shows when its campaign uses one of the App components, which the app draws natively: a
 * [TextBadge], an [ImageBadge] or [SecondaryMessaging].
 */
sealed interface PromotionComponent

/**
 * An image, from the App image badge
 */
data class ImageBadge(
    /** The image's URL */
    val imageUrl: String,

    /** Text that describes the image, for screen readers */
    val altText: String?
) : PromotionComponent

/**
 * A short text on a background, from the App text badge
 */
data class TextBadge internal constructor(
    /** The text */
    val text: String,

    private val fontColorValue: String?,
    private val backgroundValue: String?,
    private val borderColorValue: String?
) : PromotionComponent {

    /** The text's color as a color int, for example for `TextView.setTextColor`, or null when none is set */
    val fontColor: Int? = ColorUtil.parseColor(fontColorValue)

    /** The border's color as a color int, or null when none is set */
    val borderColor: Int? = ColorUtil.parseColor(borderColorValue)

    /**
     * Returns a drawable that fills its bounds with the background, a plain color or a gradient, with rounded corners
     * of [cornerRadius] pixels, to use as a view's background, or null when there is none
     */
    fun backgroundDrawable(cornerRadius: Float = 0f): Drawable? {
        return GradientParser.parseBackground(backgroundValue)?.toDrawable(cornerRadius)
    }

    /**
     * Returns a shader that paints the background, a plain color or a gradient, over an area of [width] by [height]
     * pixels, for example for a Compose `ShaderBrush`, or null when there is no background or the area is empty
     */
    fun backgroundShader(width: Float, height: Float): Shader? {
        return GradientParser.parseBackground(backgroundValue)?.toShader(width, height)
    }
}

/**
 * A line of text, from the App secondary messaging
 */
data class SecondaryMessaging internal constructor(
    /** The text */
    val text: String,

    private val fontColorValue: String?
) : PromotionComponent {

    /** The text's color as a color int, for example for `TextView.setTextColor`, or null when none is set */
    val fontColor: Int? = ColorUtil.parseColor(fontColorValue)
}
