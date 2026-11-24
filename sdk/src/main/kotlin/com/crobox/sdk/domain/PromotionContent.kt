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
     * Name of the campaign's component, as a module name, for example `MobAppTextBadgeTsx`
     */
    @SerializedName("component")
    val componentName: String
) {

    /**
     * Returns what the promotion shows when its campaign uses one of the App components (`MobAppTextBadgeTsx`,
     * `MobAppImageBadgeTsx` or `MobAppSecondaryMessagingTsx`), or null for any other component.
     */
    fun component(): PromotionComponent? {
        return when (componentName) {
            "MobAppTextBadgeTsx" -> config["text"]?.let { text ->
                TextBadge(text, config["fontColor"], config["backgroundColor"], config["borderColor"])
            }

            "MobAppImageBadgeTsx" -> config["image"]?.let { ImageBadge(imageUrl(it), config["altText"]) }
            "MobAppSecondaryMessagingTsx" -> config["text"]?.let { SecondaryMessaging(it, config["fontColor"]) }
            else -> null
        }
    }

    /**
     * The App can store a CDN image without its scheme, as `//cdn.crobox.io/...`, which an app cannot load, so that
     * gets https.
     */
    private fun imageUrl(value: String): String = if (value.startsWith("//")) "https:$value" else value
}
