package com.crobox.sdk.testapp.data.source

import com.crobox.sdk.testapp.data.model.Product
import com.crobox.sdk.testapp.data.model.Variant

class ProductDataSource {

    val product1 = Product(1, mutableListOf("https://cdn-test.xoborc.com/content/k6kszn/test/file-1.jpg"),	"Handcrafted Metal Ball",	"Table",	"white",	"Clothing",	551.00,	"Handmade",	"Metal",	49111)
    val product2 = Product(2, mutableListOf("https://cdn-test.xoborc.com/content/k6kszn/test/file-2.jpg"),	"Handmade Rubber Mouse",	"Table",	"mint green",	"Movies",	532.00,	"Handmade",	"Granite",	42786)
    val product3 = Product(3, mutableListOf("https://cdn-test.xoborc.com/content/k6kszn/test/file-3.jpg"),	"Ergonomic Soft Table",	"Chair",	"violet",	"Games",	494.00,	"Rustic",	"Cotton",	7708)
    val product4 = Product(4, mutableListOf("https://cdn-test.xoborc.com/content/k6kszn/test/file-4.jpg"),	"Gorgeous Wooden Shoes",	"Shoes",	"silver",	"Computers",	520.00,	"Practical",	"Rubber",	75154)
    val product5 = Product(5, mutableListOf("https://cdn-test.xoborc.com/content/k6kszn/test/file-5.jpg"),"Handcrafted Concrete Soap","Pizza", "orchid", "Computers", 675.00, "Awesome", "Fresh", 17537)
    val product6 = Product(6, mutableListOf("https://cdn-test.xoborc.com/content/k6kszn/test/file-6.jpg"),	"Generic Plastic Towels",	"Hat",	"purple",	"Home",	153.00,	"Handmade",	"Metal",	10854)

    val products = listOf(product1, product2, product3, product4, product5, product6)


    var variant11 = Variant(1, "36", false)
    var variant12 = Variant(1, "37")
    var variant13 = Variant(1, "38")
    var variant14 = Variant(1, "39", false)
    var variant15 = Variant(1, "40")

    val variants = listOf(variant11, variant12, variant13, variant14, variant15)
}