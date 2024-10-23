package com.crobox.sdk.testapp.data.source

import com.crobox.sdk.testapp.data.model.Product
import com.crobox.sdk.testapp.data.model.PurchaseItem

class BasketDataSource {

    companion object {

        val items = mutableListOf<PurchaseItem>()

        fun addItem(product: Product, quantity: Int = 1) {
            val sameProduct = hasProduct(product)
            if (sameProduct == null) {
                items.add(PurchaseItem(product, quantity))
            }
        }

        fun removeItem(product: Product, quantity: Int = -1) {
            val sameProduct = hasProduct(product)
            if (sameProduct != null) {
                items.remove(sameProduct)
            }
        }

        fun hasProduct(product: Product): PurchaseItem? {
            return items.firstOrNull { it.product.id == product.id }
        }

        fun increment(product: Product) {
            val sameProduct = hasProduct(product)
            if (sameProduct != null) {
                sameProduct!!.quantity = sameProduct!!.quantity + 1
            }
        }

        fun decrement(product: Product) {
            val sameProduct = hasProduct(product)
            if (sameProduct != null) {
                if (sameProduct!!.quantity > 1) {
                    sameProduct!!.quantity = sameProduct!!.quantity - 1
                } else {
                    delete(product)
                }
            }
        }

        fun delete(product: Product) {
            removeItem(product)
        }

        fun clearAll() {
            items.clear()
        }
    }
}