package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName

/**
 * @property config Configuration of each individual item
 * @property id Message Id of this promotion
 */
class PromotionContent {
    @SerializedName("config") val config: PromotionConfig? = null
    @SerializedName("id") val id: String? = null
}