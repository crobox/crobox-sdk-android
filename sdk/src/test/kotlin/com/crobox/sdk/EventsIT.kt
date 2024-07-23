package com.crobox.sdk

import com.crobox.sdk.common.CurrencyCode
import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.data.model.CartQueryParams
import com.crobox.sdk.data.model.CheckoutParams
import com.crobox.sdk.data.model.ClickQueryParams
import com.crobox.sdk.data.model.ErrorQueryParams
import com.crobox.sdk.data.model.PageType
import com.crobox.sdk.data.model.PageViewParams
import com.crobox.sdk.data.model.ProductParams
import com.crobox.sdk.data.model.PurchaseParams
import com.crobox.sdk.data.model.RequestQueryParams
import org.junit.After
import org.junit.Test
import java.util.UUID

class EventsIT {

    private val containerId = "xlrc9t"

    private val croboxInstance = Crobox.getInstance(
        CroboxConfig(
            containerId = containerId,
            visitorId = UUID.randomUUID(),
            currencyCode = CurrencyCode.USD,
            localeCode = LocaleCode.EN_US
        )
    )

    private val overviewPageParams = RequestQueryParams(
        viewId = UUID.randomUUID(),
        pageType = PageType.PageOverview
    )

    private val indexPageParams = RequestQueryParams(
        viewId = UUID.randomUUID(),
        pageType = PageType.PageIndex
    )

    private val cartPageParams = RequestQueryParams(
        viewId = UUID.randomUUID(),
        pageType = PageType.PageCart
    )

    private val productId = "product1"

    @After
    fun after() {
        Thread.sleep(200)
    }

    @Test
    fun testClick() {
        croboxInstance.enableLogging()

        // Sending Click events
        croboxInstance.clickEvent(
            overviewPageParams,
            clickQueryParams = ClickQueryParams(
                productId = productId,
                price = 1.0,
                quantity = 1
            )
        )
    }

    @Test
    fun testPageView() {
        croboxInstance.enableLogging()

        // Sending Page View events
        croboxInstance.pageViewEvent(
            indexPageParams, PageViewParams(
                pageTitle = "some page title",
                product = ProductParams(
                    productId = "1",
                    price = 1.0,
                    quantity = 1,
                    otherProductIds = setOf("2", "3", "4")
                ),
                searchTerms = "some search terms",
                impressions = setOf(
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
        croboxInstance.enableLogging()
        val checkoutPage = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageCheckout
        )
        // Sending Page View events
        croboxInstance.checkoutEvent(
            checkoutPage,
            CheckoutParams(
                products = setOf(
                    ProductParams(
                        productId = "1",
                        price = 1.0,
                        quantity = 1,
                        otherProductIds = setOf("3", "5", "7")
                    ), ProductParams(
                        productId = "2",
                        price = 2.0,
                        quantity = 2,
                        otherProductIds = setOf("4", "6", "8")
                    )
                ),
                step = "1",
                customProperties = mapOf(Pair("page-specific", "true"))
            )
        )
    }

    @Test
    fun testPurchaseEvent() {
        croboxInstance.enableLogging()
        val pageComplete = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageComplete
        )
        // Sending Page View events
        croboxInstance.purchaseEvent(
            pageComplete,
            PurchaseParams(
                products = setOf(
                    ProductParams(
                        productId = "1",
                        price = 1.0,
                        quantity = 1,
                        otherProductIds = setOf("3", "5", "7")
                    ), ProductParams(
                        productId = "2",
                        price = 2.0,
                        quantity = 2,
                        otherProductIds = setOf("4", "6", "8")
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
        croboxInstance.enableLogging()

        // Sending AddToCart events
        croboxInstance.addToCartEvent(
            overviewPageParams,
            cartQueryParams = CartQueryParams(
                productId = productId,
                price = 1.0,
                quantity = 1
            )
        )

    }

    @Test
    fun testRmCart() {
        croboxInstance.enableLogging()
        // Sending Remove From Cart events
        croboxInstance.removeFromCartEvent(
            cartPageParams,
            cartQueryParams = CartQueryParams(
                productId = productId,
                price = 1.0,
                quantity = 1
            )
        )
    }

    @Test
    fun testError() {
        croboxInstance.enableLogging()

        // Sending Error events
        croboxInstance.errorEvent(
            cartPageParams,
            errorQueryParams = ErrorQueryParams(
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
        croboxInstance.enableLogging()

        // Sending two Error events
        croboxInstance.errorEvent(
            cartPageParams,
            errorQueryParams = ErrorQueryParams(
                tag = "ParsingError",
                name = "IllegalArgumentException",
                message = "bad input",
                file = "MainActivity",
                line = 100
            )
        )

        croboxInstance.errorEvent(
            cartPageParams,
            errorQueryParams = ErrorQueryParams(
                tag = "ParsingError",
                name = "IllegalArgumentException",
                message = "bad input",
                file = "MainActivity",
                line = 100
            )
        )

        croboxInstance.errorEvent(
            cartPageParams,
            errorQueryParams = ErrorQueryParams(
                tag = "ParsingError",
                name = "IllegalArgumentException",
                message = "bad input",
                file = "MainActivity",
                line = 100
            )
        )

    }
}
