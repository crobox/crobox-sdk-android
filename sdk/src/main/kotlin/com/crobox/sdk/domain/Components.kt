package com.crobox.sdk.domain

import android.graphics.Color
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

    /// Background color as hex for opaque colors or rgba for transparent colors
    val backgroundColor: String?,

    /// Border color as hex for opaque colors or rgba for transparent colors
    val borderColor: String?,

    /// Component name
    override val name: String
) : PromotionContentConfig {
    fun fontColorAndroid(): Color? {
        return toAndroidColor(fontColor)
    }

    fun backgroundColorAndroid(): Color? {
        return toAndroidColor(backgroundColor)
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