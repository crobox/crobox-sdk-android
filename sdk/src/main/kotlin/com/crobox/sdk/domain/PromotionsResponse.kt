package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName

/**
 * Promotion Result
 *
 * @property context The context about campaigns
 * @property promotions The promotions calculated
 */
class PromotionsResponse(
    @SerializedName("context") val context: PromotionContext? = null,
    @SerializedName("promotions") val promotions: List<Promotion>? = null
)