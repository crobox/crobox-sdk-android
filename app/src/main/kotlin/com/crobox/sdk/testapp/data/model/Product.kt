package com.crobox.sdk.testapp.data.model

import com.crobox.sdk.domain.PromotionContent
import kotlin.random.Random

data class Product(
    val id: Int,
    val images: List<String>,
    val name: String,
    val type: String,
    val color: String,
    val department: String,
    val price: Double,
    val productAdjective: String,
    val productMaterial: String,
    val number: Int,
    val rating: Double,
    val variants: List<Variant>
) {
    var promotionTL: PromotionContent? = null
    var promotionTR: PromotionContent? = null

    init {
        //just for a demo purposes
        promotionTL = generateTestPromotion("SALE", "#ffffff","#ff2222")
        promotionTR = generateTestPromotion("OUTLET", "#ffffff","#22ff22")
    }

    private fun generateTestPromotion(
        text: String,
        fontColor: String,
        backgroundColor: String
    ): PromotionContent? {
        val hasPromotion = Random.nextInt(3) >= 2
        if (hasPromotion) {
            return PromotionContent(
                config = mapOf(
                    "text" to text,
                    "fontColor" to color,
                    "backgroundColor" to backgroundColor,
                    "borderColor" to backgroundColor
                ),
                messageId = "",
                componentName = "mob-app-text-badge.tsx"//text view
            )
        }
        return null
    }
}