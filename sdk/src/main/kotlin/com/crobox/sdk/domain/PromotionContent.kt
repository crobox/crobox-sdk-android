package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName

data class PromotionContent (
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
    val config: Map<String, String>,

    /**
     * Message Id of this promotion
     */
    @SerializedName("id")
    val messageId: String,

    /**
     * Component Name
     */
    @SerializedName("component")
    val component: String
)