package com.crobox.sdk.domain

import android.graphics.Color
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PromotionContentTest {

    val Gson: Gson = Gson()

    @Test
    fun componentAsTextBadge() {
        val contentWithTextBadge = """
            {
                "component": "MobAppTextBadgeTsx",
                "config": {
                    "text": "text badge text",
                    "fontColor": "#aaaaaa",
                    "backgroundColor": "#bbbbbb",
                    "borderColor": "rgba(204, 204, 204, 0.5)"
                }
            }
        """.trimIndent()
        val pc = Gson.fromJson(contentWithTextBadge, PromotionContent::class.java)
        pc.component()?.let {
            when (it) {
                is TextBadge -> {
                    assertEquals("text badge text", it.text)
                    assertEquals(Color.rgb(170, 170, 170), it.fontColor)
                    assertEquals(Color.argb(128, 204, 204, 204), it.borderColor)
                    assertNotNull(it.backgroundDrawable())
                }

                else ->
                    fail("config should be of type TextBadge")

            }
        } ?: fail("config should exist")
    }

    @Test
    fun componentAsImageBadge() {
        val contentWithImageBadge = """
            {
                "component": "MobAppImageBadgeTsx",
                "config": {
                    "image": "some image url",
                    "altText": "some image altText"
                }
            }
        """.trimIndent()
        val pc = Gson.fromJson(contentWithImageBadge, PromotionContent::class.java)
        pc.component()?.let {
            when (it) {
                is ImageBadge -> {
                    assertEquals("some image url", it.imageUrl)
                    assertEquals("some image altText", it.altText)
                }

                else ->
                    fail("config should be of type Image Badge")

            }
        } ?: fail("config should exist")
    }

    @Test
    fun componentAsSecondaryMessaging() {
        val contentWithSecondaryMessaging = """
            {
                "component": "MobAppSecondaryMessagingTsx",
                "config": {
                    "text": "secondary badge text",
                    "fontColor": "#aaaaaa"
                }
            }
        """.trimIndent()

        val pc = Gson.fromJson(contentWithSecondaryMessaging, PromotionContent::class.java)
        pc.component()?.let {
            when (it) {
                is SecondaryMessaging -> {
                    assertEquals("secondary badge text", it.text)
                    assertEquals(Color.rgb(170, 170, 170), it.fontColor)
                }

                else ->
                    fail("config should be of type Secondary Messaging")

            }
        } ?: fail("config should exist")
    }

    @Test
    fun componentIsNullForOtherComponents() {
        val content = PromotionContent(
            config = mapOf("text" to "SALE", "backgroundColor" to "#ba031d"),
            messageId = "m",
            componentName = "PlpHighlightMobileTsx"
        )
        assertNull(content.component())
    }
}
