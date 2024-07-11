package com.crobox.sdk.data.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.nio.ByteBuffer
import java.util.Base64
import java.util.UUID

internal class CustomUUIDDeserializer : JsonDeserializer<UUID> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): UUID {
        val uuidEncoded = json?.asString
        return uuidEncoded?.let {
            if (it.length == 36) {
                UUID.fromString(uuidEncoded)
            } else {
                val bb = ByteBuffer.wrap(Base64.getUrlDecoder().decode(uuidEncoded))
                UUID(bb.getLong(), bb.getLong())
            }
        } ?: UUID.randomUUID()
    }
}