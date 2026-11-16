package com.crobox.sdk.domain

import android.graphics.Color
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PromotionContentTest {

    val Gson: Gson = Gson()

    @Test
    fun contentConfigAsTextBadge() {
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
        pc.contentConfig()?.let {
            when (it) {
                is TextBadge -> {
                    assertEquals("text badge text", it.text)
                    assertEquals("MobAppTextBadgeTsx", it.name)
                    assertEquals("#aaaaaa", it.fontColor)
                    assertEquals("#bbbbbb", it.backgroundColor)
                    assertEquals("rgba(204, 204, 204, 0.5)", it.borderColor)
                    assertEquals(Color.valueOf(Color.rgb(170, 170, 170)), it.fontColorAndroid())
                    assertEquals(
                        Color.valueOf(Color.rgb(187, 187, 187)),
                        it.backgroundColorAndroid()
                    )
                    assertEquals(
                        Color.valueOf(Color.argb(128, 204, 204, 204)),
                        it.borderColorAndroid()
                    )
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
                "component": "MobAppImageBadgeTsx",
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
                    assertEquals("MobAppImageBadgeTsx", it.name)
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
                "component": "MobAppSecondaryMessagingTsx",
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
                    assertEquals("MobAppSecondaryMessagingTsx", it.name)
                    assertEquals("#aaaaaa", it.fontColor)
                }

                else ->
                    fail("config should be of type Secondary Messaging")

            }
        } ?: fail("config should exist")
    }

    @Test
    fun contentConfigDoesNotDependOnTheComponentName() {
        val content = PromotionContent(
            config = mapOf("text" to "SALE", "backgroundColor" to "#ba031d"),
            messageId = "m",
            componentName = "AnyOtherName"
        )
        assertTrue(content.contentConfig() is TextBadge)
    }

    @Test
    fun contentConfigIsNullForOtherComponents() {
        val content = PromotionContent(
            config = mapOf("Text1_text" to "SALE"),
            messageId = "m",
            componentName = "PlpHighlightMobileTsx"
        )
        assertNull(content.contentConfig())
    }
}
