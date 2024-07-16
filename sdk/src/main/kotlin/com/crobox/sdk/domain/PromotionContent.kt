package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName

data class PromotionContent(
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
) {

    /**
     * Returns an Image Badge if an image component exists with the following pre-defined keys: "image" and "altText"
     */
    fun getImageBadge(): ImageBadge? {
        return config["image"]?.let { ImageBadge(it, config["altText"]) }
    }

    /**
     * Returns a Text Badge if a text component exists with the following pre-defined keys: "text", "fontColor", "backgroundColor" and "borderColor"
     */
    fun getTextBadge(): TextBadge? {
        return config["text"]?.let { text ->
            config["fontColor"]?.let { fontColor ->
                TextBadge(text, fontColor, config["backgroundColor"], config["borderColor"])
            }
        }
    }
}

