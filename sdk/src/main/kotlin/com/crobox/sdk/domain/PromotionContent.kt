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
    val componentName: String
) {

    /**
     * Returns component if an Image, Text Badge or Secondary Messaging is available
     */
    fun contentConfig(): PromotionContentConfig? {
        return when (componentName) {
            "mob-app-image-badge.tsx" -> getImageBadge()
            "mob-app-text-badge.tsx" -> getTextBadge()
            "mob-app-secondary-messaging.tsx" -> getSecondaryMessaging()
            else -> null
        }
    }

    /**
     * Returns an Image Badge if an image component exists with the following pre-defined keys: "image" and "altText"
     */
    private fun getImageBadge(): ImageBadge? {
        return config["image"]?.let { ImageBadge(it, config["altText"], componentName) }
    }

    /**
     * Returns a Text Badge if a text component exists with the following pre-defined keys: "text", "fontColor", "backgroundColor" and "borderColor"
     */
    private fun getTextBadge(): TextBadge? {
        return config["text"]?.let { text ->
            TextBadge(
                text,
                config["fontColor"],
                config["backgroundColor"],
                config["borderColor"],
                componentName
            )

        }
    }

    /**
     * Returns Secondary Messaging if a text component exists with the following pre-defined keys: "text", "fontColor"
     */
    private fun getSecondaryMessaging(): SecondaryMessaging? {
        return config["text"]?.let { text ->
            SecondaryMessaging(
                text,
                config["fontColor"],
                componentName
            )

        }
    }
}

