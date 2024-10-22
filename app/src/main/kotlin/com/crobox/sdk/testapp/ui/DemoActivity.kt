package com.crobox.sdk.testapp.ui

import android.os.Bundle
import com.crobox.sdk.common.CurrencyCode
import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.testapp.R
import com.crobox.sdk.testapp.data.CroboxRequests
import com.crobox.sdk.testapp.data.model.Product
import com.crobox.sdk.testapp.ui.base.BaseActivity
import java.util.UUID

class DemoActivity : BaseActivity() {

    // Use the Container ID assigned by Crobox
    private val containerId = "xlrc9t"

    // Collection of products/impressions
    private val impressions: List<String> =
        listOf("1", "product1", "product2", "product3", "product4", "product5")
    private val productId = impressions[0]

    // CroboxInstance is the single point of all interactions, keeping the configuration and providing all functionality
    private val croboxInstance = Crobox.getInstance(
        CroboxConfig(
            containerId = containerId,
            visitorId = UUID.randomUUID(),
            currencyCode = CurrencyCode.USD,
            localeCode = LocaleCode.EN_US
        )
    )

    private var croboxRequests = CroboxRequests(croboxInstance)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_activity)
        croboxRequests.executeTestRequests(productId, impressions)

        showProductList()
    }

    fun showProductList() {
        replaceFragment(ProductListFragment())
    }

    fun showProductDetails(product: Product) {
        replaceFragment(ProductFragment())
    }
}