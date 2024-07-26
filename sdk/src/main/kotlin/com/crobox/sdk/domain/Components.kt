package com.crobox.sdk.domain

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

    /// Font color
    val fontColor: String?,

    /// Background color
    val backgroundColor: String?,

    /// Border color
    val borderColor: String?,

    /// Component name
    override val name: String
) : PromotionContentConfig

data class SecondaryMessaging(
    /// Text message
    val text: String,

    /// Font color
    val fontColor: String?,

    /// Component name
    override val name: String
) : PromotionContentConfig