package com.crobox.sdk.domain

/**
 * Visual configuration elements.
 *
 * Example:
 * Map(
 *  "Text1_text" : "Best Seller",
 *   "Text1_color" : "#0e1111"
 *  )
 * @property data Map of all visual configuration items, managed via Crobox Admin app
 *
 */
class PromotionConfig(jsonData: Map<String, String>) {
    val data: MutableMap<String, String> = mutableMapOf()
    init {
        data.putAll(jsonData)
    }
    fun getValue(key: String): String? {
        return data[key]
    }
    fun getValue(key: String, defaultValue: String): String {
        return data[key] ?: defaultValue
    }
}