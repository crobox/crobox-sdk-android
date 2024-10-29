package com.crobox.sdk.testapp.data.source

import com.crobox.sdk.testapp.data.model.Product
import com.crobox.sdk.testapp.data.model.Variant
import kotlin.random.Random

class ProductDataSource {

    companion object {

        fun getTestImageUrl(id: Int): String {
            return "https://picsum.photos/id/$id/200/200"
        }

        val product1 = Product(
            1,
            mutableListOf(getTestImageUrl(0), getTestImageUrl(1)),
            "Handcrafted Metal Ball",
            "Table",
            "white",
            "Clothing",
            551.00,
            "Handmade",
            "Metal",
            49111,
            4.5,
            generateVariants(1, 35, 45)
        )
        val product2 = Product(
            2,
            mutableListOf(getTestImageUrl(2), getTestImageUrl(3)),
            "Handmade Rubber Mouse",
            "Table",
            "mint green",
            "Movies",
            532.00,
            "Handmade",
            "Granite",
            42786,
            4.0,
            generateVariants(2, 36, 47)
        )
        val product3 = Product(
            3,
            mutableListOf(getTestImageUrl(4), getTestImageUrl(5)),
            "Ergonomic Soft Table",
            "Chair",
            "violet",
            "Games",
            494.00,
            "Rustic",
            "Cotton",
            7708,
            3.0,
            generateVariants(3, 39, 45)
        )
        val product4 = Product(
            4,
            mutableListOf(getTestImageUrl(6), getTestImageUrl(7)),
            "Gorgeous Wooden Shoes",
            "Shoes",
            "silver",
            "Computers",
            520.00,
            "Practical",
            "Rubber",
            75154,
            5.0,
            generateVariants(1, 36, 45)
        )
        val product5 = Product(
            5,
            mutableListOf(getTestImageUrl(8), getTestImageUrl(9)),
            "Handcrafted Concrete Soap",
            "Pizza",
            "orchid",
            "Computers",
            675.00,
            "Awesome",
            "Fresh",
            17537,
            4.8,
            generateVariants(1, 40, 47)
        )
        val product6 = Product(
            6,
            mutableListOf(getTestImageUrl(10), getTestImageUrl(11)),
            "Generic Plastic Towels",
            "Hat",
            "purple",
            "Home",
            153.00,
            "Handmade",
            "Metal",
            10854,
            1.0,
            generateVariants(1, 34, 45)
        )

        val products = listOf(product1, product2, product3, product4, product5, product6)

        fun generateVariants(productId: Int, minValue: Int, maxValue: Int): List<Variant> {
            val range = minValue..maxValue
            return range.map { value ->
                val isRandom = Random.nextBoolean() // randomly choose true or false
                Variant(productId, "$value", isRandom)
            }
        }
    }
}