package com.crobox.sdk.data.api

import com.crobox.sdk.domain.Promotion
import com.crobox.sdk.domain.PromotionContent
import com.crobox.sdk.domain.PromotionContext
import com.crobox.sdk.domain.PromotionsResponse
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.util.UUID

/**
 * Reads a promotions response, skipping promotions that cannot be read instead of failing the whole response, as the
 * iOS SDK does. A promotion without a valid id is skipped.
 */
internal class PromotionsResponseDeserializer : JsonDeserializer<PromotionsResponse> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): PromotionsResponse {
        val obj = json.takeIf { it.isJsonObject }?.asJsonObject
            ?: throw JsonParseException("Promotions response is not an object")
        val promotionContext: PromotionContext = context.deserialize(obj.get("context"), PromotionContext::class.java)
            ?: throw JsonParseException("Promotions response has no context")

        val promotions = obj.get("promotions")?.takeIf { it.isJsonArray }?.asJsonArray
            ?.mapNotNull { element ->
                runCatching { context.deserialize<Promotion?>(element, Promotion::class.java) }.getOrNull()
                    // Gson leaves a missing id null, although the type says otherwise
                    ?.takeIf { (it.id as UUID?) != null }
            }
            ?: emptyList()

        return PromotionsResponse(promotionContext, promotions)
    }
}

/**
 * Reads promotion content leniently, as the iOS SDK does: numbers and booleans in `config` become strings, and values
 * that cannot be a string (null, objects, arrays) are skipped, so that one unexpected value does not discard the
 * promotion. A missing id or component becomes an empty string.
 */
internal class PromotionContentDeserializer : JsonDeserializer<PromotionContent> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): PromotionContent {
        val obj = json.takeIf { it.isJsonObject }?.asJsonObject
            ?: throw JsonParseException("Promotion content is not an object")

        val config = obj.get("config")?.takeIf { it.isJsonObject }?.asJsonObject
            ?.entrySet()
            ?.mapNotNull { (key, value) ->
                value.takeIf { it.isJsonPrimitive }?.let { key to it.asString }
            }
            ?.toMap()
            ?: emptyMap()

        return PromotionContent(
            config = config,
            messageId = obj.stringOrEmpty("id"),
            componentName = obj.stringOrEmpty("component")
        )
    }

    private fun com.google.gson.JsonObject.stringOrEmpty(key: String): String =
        get(key)?.takeIf { it.isJsonPrimitive }?.asString ?: ""
}
