package com.crobox.sdk.domain

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class PromotionContentTest {

    val Gson: Gson = Gson()

    @Test
    fun contentConfigAsTextBadge() {
        val contentWithTextBadge = """
            {
                "component": "${"mob-app-text-badge.tsx"}",
                "config": {
                    "text": "text badge text",
                    "fontColor": "#aaaaaa",
                    "backgroundColor": "#bbbbbb",
                    "borderColor": "#cccccc"
                }
            }
        """.trimIndent()
        val pc = Gson.fromJson(contentWithTextBadge, PromotionContent::class.java)
        pc.contentConfig()?.let {
            when (it) {
                is TextBadge -> {
                    assertEquals("text badge text", it.text)
                    assertEquals("mob-app-text-badge.tsx", it.name)
                    assertEquals("#aaaaaa", it.fontColor)
                    assertEquals("#bbbbbb", it.backgroundColor)
                    assertEquals("#cccccc", it.borderColor)
                }

                else ->
                    fail("config should be of type TextBadge")

            }
        } ?: fail("config should exist")
    }

    @Test
    fun contentConfigAsImageBadge() {
        val contentWithImageBadge = """
            {
                "component": "mob-app-image-badge.tsx",
                "config": {
                    "image": "some image url",
                    "altText": "some image altText"
                }
            }
        """.trimIndent()
        val pc = Gson.fromJson(contentWithImageBadge, PromotionContent::class.java)
        pc.contentConfig()?.let {
            when (it) {
                is ImageBadge -> {
                    assertEquals("some image url", it.image)
                    assertEquals("mob-app-image-badge.tsx", it.name)
                    assertEquals("some image altText", it.altText)
                }

                else ->
                    fail("config should be of type Image Badge")

            }
        } ?: fail("config should exist")
    }

    @Test
    fun contentConfigAsSecondaryMessaging() {
        val contentWithSecondaryMessaging = """
            {
                "component": "${"mob-app-secondary-messaging.tsx"}",
                "config": {
                    "text": "secondary badge text",
                    "fontColor": "#aaaaaa"
                }
            }
        """.trimIndent()

        val pc = Gson.fromJson(contentWithSecondaryMessaging, PromotionContent::class.java)
        pc.contentConfig()?.let {
            when (it) {
                is SecondaryMessaging -> {
                    assertEquals("secondary badge text", it.text)
                    assertEquals("mob-app-secondary-messaging.tsx", it.name)
                    assertEquals("#aaaaaa", it.fontColor)
                }

                else ->
                    fail("config should be of type Secondary Messaging")

            }
        } ?: fail("config should exist")
    }
}