package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName

class PromotionsResponse(
    @SerializedName("context") val context: PromotionContext? = null,
    @SerializedName("promotions") val promotions: List<Promotion>? = null
)