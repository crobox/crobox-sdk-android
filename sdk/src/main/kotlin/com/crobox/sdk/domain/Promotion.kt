package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName

/**
 * Represents a promotion calculated
 *
 * @property id Unique id for this promotion
 * @property productId Product ID that this promotion was requested for
 * @property campaignId The campaign which this promotion belongs to
 * @property variantId The variant which this promotion belongs to
 * @property content Promotion Content
 */
class Promotion {
    @SerializedName("id") val id: String? = null
    @SerializedName("productId") val productId: String? = null
    @SerializedName("campaignId")val campaignId: Int? = null
    @SerializedName("variantId")val variantId: Int? = null
    @SerializedName("content") val content:  PromotionContent? = null
}

