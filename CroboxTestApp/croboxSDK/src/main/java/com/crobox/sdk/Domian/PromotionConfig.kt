package com.crobox.sdk.Domian

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