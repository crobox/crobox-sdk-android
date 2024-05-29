package com.crobox.sdk.Domian

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PromotionsResponse {

    @SerializedName("promotions")
    @Expose
    val promotions:  List<Promotion>? = null

    @SerializedName("context")
    @Expose
    val context: PromotionContext? = null
}