package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * Represents a promotion calculated
 */
data class Promotion (
    /**
     *  Unique id for this promotion
     */
    @SerializedName("id")
    val id: UUID,

    /**
     *  Product ID that this promotion was requested for
     */
    @SerializedName("productId")
    val productId: String?,

    /**
     *  The campaign which this promotion belongs to
     */
    @SerializedName("campaignId")
    val campaignId: Int,

    /**
     *  The variant which this promotion belongs to
     */
    @SerializedName("variantId")
    val variantId: Int,

    /**
     *  Promotion Content
     */
    @SerializedName("content")
    val content: PromotionContent?
)
