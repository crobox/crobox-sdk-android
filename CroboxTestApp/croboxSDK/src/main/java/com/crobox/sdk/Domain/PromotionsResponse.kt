package com.crobox.sdk.Domain
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PromotionsResponse(
    @SerializedName("context") val context: PromotionContext? = null,
    @SerializedName("promotions") val promotions: List<Promotion>? = null
)