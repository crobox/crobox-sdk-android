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
     * Name of the component configured for the campaign, as a module name, for example `MobAppTextBadgeTsx`
     */
    @SerializedName("component")
    val componentName: String
) {

    /**
     * Returns the content as an Image Badge, Text Badge or Secondary Messaging, based on the keys in [config]:
     *  - "image" (and optionally "altText"): [ImageBadge]
     *  - "text" with "backgroundColor" or "borderColor" (and optionally "fontColor"): [TextBadge]
     *  - "text" (and optionally "fontColor"): [SecondaryMessaging]
     *
     * Returns null when the config has none of these, for example for a component of your own; use [config] and
     * [componentName] directly in that case.
     */
    fun contentConfig(): PromotionContentConfig? = when {
        config.containsKey("image") -> getImageBadge()
        config.containsKey("text") && (config.containsKey("backgroundColor") || config.containsKey("borderColor")) ->
            getTextBadge()
        config.containsKey("text") -> getSecondaryMessaging()
        else -> null
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

