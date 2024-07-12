package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName

/**
 * Promotion Result
 */
data class PromotionsResponse(
    /**
     * The context about campaigns
     */
    @SerializedName("context") val context: PromotionContext,
    /**
     * The promotions calculated
     */
    @SerializedName("promotions") val promotions: List<Promotion>
)