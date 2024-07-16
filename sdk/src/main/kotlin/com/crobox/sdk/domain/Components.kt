package com.crobox.sdk.domain

data class ImageBadge(
    ///
    val image: String,
    ///
    val altText: String?
)

data class TextBadge (
    ///
    val text: String,
    ///
    val fontColor: String,
    ///
    val backgroundColor: String?,
    ///
    val borderColor: String?
)