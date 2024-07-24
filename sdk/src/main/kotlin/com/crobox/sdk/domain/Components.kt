package com.crobox.sdk.domain

enum class PromotionContentType { ImageBadge, TextBadge }

interface PromotionContentConfig {
    val contentType: PromotionContentType
    val name: String
}

data class ImageBadge(
    /// Image url
    val image: String,

    /// Alternate Text
    val altText: String?,

    /// Component name
    override val name: String,

    ) : PromotionContentConfig {
    override val contentType: PromotionContentType = PromotionContentType.ImageBadge
}

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
) : PromotionContentConfig {
    override val contentType: PromotionContentType = PromotionContentType.TextBadge
}