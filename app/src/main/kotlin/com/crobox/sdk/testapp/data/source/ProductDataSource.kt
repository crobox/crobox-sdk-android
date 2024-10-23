package com.crobox.sdk.testapp.data.source

import com.crobox.sdk.testapp.data.model.Product
import com.crobox.sdk.testapp.data.model.Variant

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
            49111
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
            42786
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
            7708
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
            75154
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
            17537
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
            10854
        )

        val products = listOf(product1, product2, product3, product4, product5, product6)


        var variant11 = Variant(1, "36", false)
        var variant12 = Variant(1, "37")
        var variant13 = Variant(1, "38")
        var variant14 = Variant(1, "39", false)
        var variant15 = Variant(1, "40")

        val variants = listOf(variant11, variant12, variant13, variant14, variant15)
    }
}