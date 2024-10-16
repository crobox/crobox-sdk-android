package com.crobox.sdk.testapp.data

data class Product(val id: Int,
                   val images: List<String>,
                   val name: String,
                   val type: String,
                   val color: String,
                   val department: String,
                   val price: Double,
                   val productAdjective: String,
                   val productMaterial: String,
                    val number: Int)