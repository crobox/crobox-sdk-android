package com.crobox.sdk.testapp.ui

import android.os.Bundle
import com.crobox.sdk.common.CroboxVisitor
import com.crobox.sdk.common.CurrencyCode
import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.testapp.R
import com.crobox.sdk.testapp.data.CroboxRequests
import com.crobox.sdk.testapp.data.model.Product
import com.crobox.sdk.testapp.data.model.PurchaseItem
import com.crobox.sdk.testapp.ui.base.BaseActivity
import com.crobox.sdk.testapp.ui.basket.BasketFragment
import com.crobox.sdk.testapp.ui.product.ProductFragment
import com.crobox.sdk.testapp.ui.product.ProductListFragment
import com.crobox.sdk.testapp.ui.purchase.PurchaseFragment

class DemoActivity : BaseActivity() {

    // Use the Container ID assigned by Crobox
    private val containerId = "xlrc9t"

    // Collection of products/impressions
    private val impressions: List<String> =
        listOf("1", "product1", "product2", "product3", "product4", "product5")
    private val productId = impressions[0]

    // CroboxInstance is the single point of all interactions, keeping the configuration and providing all functionality.
    // The visitorId is stored on the device, so the user stays the same visitor across app launches.
    private val croboxInstance by lazy {
        Crobox.getInstance(
            CroboxConfig(
                containerId = containerId,
                visitorId = CroboxVisitor.visitorId(this),
                currencyCode = CurrencyCode.USD,
                localeCode = LocaleCode.EN_US
            )
        )
    }

    private val croboxRequests by lazy { CroboxRequests(croboxInstance) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_activity)
        croboxRequests.setImpressions(impressions)
        croboxRequests.getPromotions()

        showProductList()
    }

    fun showProductList() {
        replaceFragment(ProductListFragment())
    }

    fun showProductDetails(product: Product) {
        replaceFragment(ProductFragment.newInstance(product))
    }

    fun showProductDetails(product: PurchaseItem) {
        replaceFragment(ProductFragment.newInstance(product))
    }

    fun addToBasket(product: Product) {
        onBackPressed()
        showBasket()
    }

    fun showBasket() {
        val dialog = BasketFragment()
        dialog.show(supportFragmentManager, "basket")
    }

    fun showPurchase() {
        val dialog = PurchaseFragment()
        dialog.show(supportFragmentManager, "purchase")
    }

    fun getCroboxAPI(): CroboxRequests {
        return croboxRequests
    }
}