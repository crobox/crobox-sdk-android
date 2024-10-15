package com.crobox.sdk.testapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crobox.sdk.common.CurrencyCode
import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.testapp.R
import com.crobox.sdk.testapp.data.CroboxRequests
import com.crobox.sdk.testapp.data.Product
import com.crobox.sdk.testapp.ui.adapter.ProductAdapter
import java.util.UUID

class MainActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_main2)
        croboxRequests.executeTestRequests(productId, impressions)

        initView()
    }

    private fun initView() {
        val product1 = Product(name = "Shoes", description = "Mens shoes", price = "$ 12.99", subImages = mutableListOf("1", "2", "3"))
        val product2 = Product("Flips", description = "Women's shoes", price = "$ 25.99", subImages = mutableListOf("1", "2"))
        val product3 = Product("Flops", description = "Unisex shoes", price = "$ 43.99", subImages = mutableListOf("1", "2"))
        val dataset = arrayOf(product1, product2, product3)

        val customAdapter = ProductAdapter(dataset)

        val recyclerView: RecyclerView = findViewById(R.id.product_list)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = customAdapter
    }

}