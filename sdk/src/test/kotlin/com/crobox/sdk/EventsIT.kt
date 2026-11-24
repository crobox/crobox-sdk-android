package com.crobox.sdk

import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.data.model.CartParams
import com.crobox.sdk.data.model.CheckoutParams
import com.crobox.sdk.data.model.ClickParams
import com.crobox.sdk.data.model.ErrorParams
import com.crobox.sdk.data.model.ProductParams
import com.crobox.sdk.data.model.PurchaseParams
import com.crobox.sdk.data.model.ViewContext
import com.crobox.sdk.data.model.ViewParams
import com.crobox.sdk.data.model.ViewType
import org.junit.After
import org.junit.Test
import java.util.UUID

class EventsIT {

    private val containerId = "xlrc9t"

    private val croboxInstance = Crobox.getInstance(
        CroboxConfig(
            containerId = containerId,
            visitorId = UUID.randomUUID(),
            localeCode = LocaleCode("en_US")
        )
    )

    private val overviewView = ViewContext(
        viewId = UUID.randomUUID(),
        viewType = ViewType.Overview
    )

    private val indexView = ViewContext(
        viewId = UUID.randomUUID(),
        viewType = ViewType.Index
    )

    private val cartView = ViewContext(
        viewId = UUID.randomUUID(),
        viewType = ViewType.Cart
    )

    private val productId = "product1"

    @After
    fun after() {
        Thread.sleep(200)
    }

    @Test
    fun testClick() {
        croboxInstance.isLoggingEnabled = true

        // Sending Click events
        croboxInstance.clickEvent(
            overviewView,
            params = ClickParams(
                productId = productId,
                quantity = 1
            )
        )
    }

    @Test
    fun testView() {
        croboxInstance.isLoggingEnabled = true

        // Sending View events
        croboxInstance.viewEvent(
            indexView, ViewParams(
                title = "some page title",
                product = ProductParams(
                    productId = "1",
                    quantity = 1
                ),
                searchTerms = "some search terms",
                impressions = listOf(
                    ProductParams(productId = "5"),
                    ProductParams(productId = "6"),
                    ProductParams(productId = "7")
                ),
                customProperties = mapOf(Pair("page-specific", "true"))
            )
        )
    }

    @Test
    fun testCheckoutEvent() {
        croboxInstance.isLoggingEnabled = true
        val checkoutView = ViewContext(
            viewId = UUID.randomUUID(),
            viewType = ViewType.Checkout
        )
        // Sending Page View events
        croboxInstance.checkoutEvent(
            checkoutView,
            CheckoutParams(
                products = listOf(
                    ProductParams(
                        productId = "1",
                        quantity = 1
                    ), ProductParams(
                        productId = "2",
                        quantity = 2
                    )
                ),
                step = "1",
                customProperties = mapOf(Pair("page-specific", "true"))
            )
        )
    }

    @Test
    fun testPurchaseEvent() {
        croboxInstance.isLoggingEnabled = true
        val completeView = ViewContext(
            viewId = UUID.randomUUID(),
            viewType = ViewType.Complete
        )
        // Sending Page View events
        croboxInstance.purchaseEvent(
            completeView,
            PurchaseParams(
                products = listOf(
                    ProductParams(
                        productId = "1",
                        quantity = 1
                    ), ProductParams(
                        productId = "2",
                        quantity = 2
                    )
                ),
                transactionId = "abc123",
                affiliation = "google store",
                coupon = "some coupon",
                revenue = 5.0,
                customProperties = mapOf(Pair("page-specific", "true"))
            )
        )
    }

    @Test
    fun testAddCart() {
        croboxInstance.isLoggingEnabled = true

        // Sending AddToCart events
        croboxInstance.addToCartEvent(
            overviewView,
            params = CartParams(
                productId = productId,
                quantity = 1
            )
        )

    }

    @Test
    fun testRmCart() {
        croboxInstance.isLoggingEnabled = true
        // Sending Remove From Cart events
        croboxInstance.removeFromCartEvent(
            cartView,
            params = CartParams(
                productId = productId,
                quantity = 1
            )
        )
    }

    @Test
    fun testError() {
        croboxInstance.isLoggingEnabled = true

        // Sending Error events
        croboxInstance.errorEvent(
            cartView,
            params = ErrorParams(
                tag = "ParsingError",
                name = "IllegalArgumentException",
                message = "bad input",
                file = "MainActivity",
                line = 100
            )
        )

    }

    @Test
    fun testCounter_e() {
        croboxInstance.isLoggingEnabled = true

        // Sending two Error events
        croboxInstance.errorEvent(
            cartView,
            params = ErrorParams(
                tag = "ParsingError",
                name = "IllegalArgumentException",
                message = "bad input",
                file = "MainActivity",
                line = 100
            )
        )

        croboxInstance.errorEvent(
            cartView,
            params = ErrorParams(
                tag = "ParsingError",
                name = "IllegalArgumentException",
                message = "bad input",
                file = "MainActivity",
                line = 100
            )
        )

        croboxInstance.errorEvent(
            cartView,
            params = ErrorParams(
                tag = "ParsingError",
                name = "IllegalArgumentException",
                message = "bad input",
                file = "MainActivity",
                line = 100
            )
        )

    }
}
