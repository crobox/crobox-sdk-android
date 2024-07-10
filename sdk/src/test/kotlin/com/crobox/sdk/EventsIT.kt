package com.crobox.sdk

import com.crobox.sdk.common.CurrencyCode
import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.data.model.CartQueryParams
import com.crobox.sdk.data.model.ClickQueryParams
import com.crobox.sdk.data.model.ErrorQueryParams
import com.crobox.sdk.data.model.PageType
import com.crobox.sdk.data.model.RequestQueryParams
import org.junit.After
import org.junit.Test
import java.util.UUID

class EventsIT {

    private val containerId = "4ekjfx"

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

    @After
    fun after () {
        Thread.sleep(200)
    }

    @Test
    fun testClick() {
        croboxInstance.enableLogging()

        // Sending Click events
        croboxInstance.clickEvent(
            overviewPageParams,
            clickQueryParams = ClickQueryParams(
                productId = "0001ABC",
                price = 1.0,
                quantity = 1
            )
        )
    }

    @Test
    fun testPageView() {
        croboxInstance.enableLogging()

        // Sending Page View events
        croboxInstance.pageViewEvent(indexPageParams)
    }

    @Test
    fun testAddCart() {
        croboxInstance.enableLogging()

        // Sending AddToCart events
        croboxInstance.addToCartEvent(
            overviewPageParams,
            cartQueryParams = CartQueryParams(
                productId = "001ABC",
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
                productId = "001ABC",
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
}