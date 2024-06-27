package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName

class Promotion {
    @SerializedName("id") val id: String? = null
    @SerializedName("productId") val productId: String? = null
    @SerializedName("experimentId") val experimentId: Int? = null
    @SerializedName("variantId")val variantId: Int? = null
    @SerializedName("campaignId")val campaignId: Int? = null
    @SerializedName("content") val content:  PromotionContent? = null
}

