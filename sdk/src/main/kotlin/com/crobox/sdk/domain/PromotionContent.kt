package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName

class PromotionContent {
    /**
     * Map of all visual configuration items, managed via Crobox Admin app
     *
     * Example:
     * Map(
     *   "Text1_text" : "Best Seller",
     *   "Text1_color" : "#0e1111"
     *  )
     */
    @SerializedName("config")
    val config: Map<String, String>? = null

    /**
     * Message Id of this promotion
     */
    @SerializedName("id")
    val id: String? = null

    /**
     * Component Name
     */
    @SerializedName("component")
    val component: String? = null
}