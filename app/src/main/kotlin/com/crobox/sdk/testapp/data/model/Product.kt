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
    var promotion: PromotionContent? = null

    init {
        //just for a demo purposes
        val hasPromotion = Random.nextInt(3) >= 2
        if(hasPromotion) {
            promotion = PromotionContent(
                config = mapOf(),
                messageId = "",
                componentName = "testPromotion"
            )
        }
    }
}