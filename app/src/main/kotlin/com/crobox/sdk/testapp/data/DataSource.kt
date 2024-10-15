package com.crobox.sdk.testapp.data

class DataSource {
    val product1 = Product(name = "Shoes", description = "Mens shoes", price = "$ 12.99", subImages = mutableListOf("1", "2", "3"))
    val product2 = Product("Flips", description = "Women's shoes", price = "$ 25.99", subImages = mutableListOf("1", "2"))
    val product3 = Product("Flops", description = "Unisex shoes", price = "$ 43.99", subImages = mutableListOf("1", "2"))
    val product4 = Product(name = "Shoes 2", description = "Mens shoes", price = "$ 34.99", subImages = mutableListOf("1", "2", "3"))
    val product5 = Product("Flips 2", description = "Women's shoes", price = "$ 67.99", subImages = mutableListOf("1", "2"))
    val product6 = Product("Flops 2", description = "Unisex shoes", price = "$ 28.99", subImages = mutableListOf("1", "2"))
    val products = arrayOf(product1, product2, product3, product4, product5, product6)
}