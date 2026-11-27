package com.crobox.sdk.data.api

import com.crobox.sdk.domain.PromotionsResponse
import com.google.gson.JsonParseException
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class LenientDeserializersTest {

    private val visitorId = UUID.randomUUID()
    private val sessionId = UUID.randomUUID()
    private val promotionId = UUID.randomUUID()

    private fun response(promotions: String): PromotionsResponse = CroboxAPIClient.gson.fromJson(
        """
        {
          "context": {"pid": "$visitorId", "sid": "$sessionId", "groupName": "g",
            "experiments": [{"id": 7, "name": "Badges", "variantId": 8, "variantName": "Crobox", "control": false}]},
          "promotions": $promotions
        }
        """.trimIndent(),
        PromotionsResponse::class.java
    )

    private fun promotion(id: String, config: String = """{"text": "Best Seller"}""") =
        """{"id": "$id", "productId": "p1", "campaignId": 1,
            "content": {"id": "m1", "component": "MobAppTextBadgeTsx", "config": $config}}"""

    @Test
    fun readsAValidResponse() {
        val response = response("[${promotion("$promotionId")}]")

        assertEquals(visitorId, response.context.visitorId)
        assertEquals(sessionId, response.context.sessionId)
        val campaign = response.context.campaigns.single()
        assertEquals(7, campaign.id)
        assertEquals(8, campaign.variantId)
        val promotion = response.promotions.single()
        assertEquals(promotionId, promotion.id)
        assertEquals("p1", promotion.productId)
        assertEquals(1, promotion.campaignId)
        assertEquals("m1", promotion.content?.messageId)
        assertEquals("MobAppTextBadgeTsx", promotion.content?.componentName)
        assertEquals(mapOf("text" to "Best Seller"), promotion.content?.config)
    }

    @Test
    fun skipsPromotionsThatCannotBeRead() {
        val response = response(
            """[
              ${promotion("not-a-uuid")},
              {"productId": "no id"},
              "not an object",
              ${promotion("$promotionId")}
            ]"""
        )

        assertEquals(listOf(promotionId), response.promotions.map { it.id })
    }

    @Test
    fun keepsScalarConfigValuesAsStringsAndSkipsOthers() {
        val config = """{"text": "Sale", "size": 12, "bold": true, "empty": null, "nested": {"a": 1}, "list": [1]}"""

        val content = response("[${promotion("$promotionId", config)}]").promotions.single().content

        assertEquals(mapOf("text" to "Sale", "size" to "12", "bold" to "true"), content?.config)
    }

    @Test(expected = JsonParseException::class)
    fun aContextIdThatIsNotAStandardUuidFailsTheResponse() {
        CroboxAPIClient.gson.fromJson(
            """{"context": {"pid": "AAECAwQFBgcICQoLDA0ODw", "sid": "$sessionId", "experiments": []}, "promotions": []}""",
            PromotionsResponse::class.java
        )
    }

    @Test
    fun missingPromotionsAreAnEmptyList() {
        val response: PromotionsResponse = CroboxAPIClient.gson.fromJson(
            """{"context": {"pid": "$visitorId", "sid": "$sessionId", "experiments": []}}""",
            PromotionsResponse::class.java
        )

        assertEquals(emptyList<Any>(), response.promotions)
    }
}
