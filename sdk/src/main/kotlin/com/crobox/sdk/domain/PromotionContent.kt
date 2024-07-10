package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName

/**
 * @property config  Map of all visual configuration items, managed via Crobox Admin app
 * @property id Message Id of this promotion
 */
class PromotionContent {
    /**
     * Visual configuration elements.
     *
     * Example:
     * Map(
     *   "Text1_text" : "Best Seller",
     *   "Text1_color" : "#0e1111"
     *  )
     */
    @SerializedName("config") val config: Map<String, String>? = null
    @SerializedName("id") val id: String? = null
    @SerializedName("component") val component: String? = null
}