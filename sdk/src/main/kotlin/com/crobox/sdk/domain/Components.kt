package com.crobox.sdk.domain

data class ImageBadge(
    /// Image url
    val image: String,

    /// Alternate Text
    val altText: String?
)

data class TextBadge (
    /// Text message
    val text: String,

    /// Font color
    val fontColor: String,

    /// Background color
    val backgroundColor: String?,

    /// Border color
    val borderColor: String?
)