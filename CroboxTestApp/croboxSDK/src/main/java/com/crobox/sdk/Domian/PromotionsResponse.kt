package com.crobox.sdk.Domian
import com.google.gson.annotations.SerializedName

class PromotionsResponse {
    @SerializedName("promotions") val promotions:  List<Promotion>? = null
    @SerializedName("context") val context: PromotionContext? = null
}