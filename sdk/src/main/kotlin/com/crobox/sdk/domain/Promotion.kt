package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName

/**
 * Represents a promotion calculated
 */
class Promotion {
    /**
     *  Unique id for this promotion
     */
    @SerializedName("id")
    val id: String? = null

    /**
     *  Product ID that this promotion was requested for
     */
    @SerializedName("productId")
    val productId: String? = null

    /**
     *  The campaign which this promotion belongs to
     */
    @SerializedName("campaignId")
    val campaignId: Int? = null

    /**
     *  The variant which this promotion belongs to
     */
    @SerializedName("variantId")
    val variantId: Int? = null

    /**
     *  Promotion Content
     */
    @SerializedName("content")
    val content: PromotionContent? = null
}

