package com.crobox.sdk.testapp.data

import android.util.Log
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
import com.crobox.sdk.domain.ImageBadge
import com.crobox.sdk.domain.PromotionsResponse
import com.crobox.sdk.domain.SecondaryMessaging
import com.crobox.sdk.domain.TextBadge
import com.crobox.sdk.presenter.PromotionCallback
import java.util.UUID

class CroboxRequests(val croboxInstance: Crobox) {

    private var productId: String = ""
    private var impressions = mutableListOf<String>()

    fun executeTestRequests(productId: String, impressions: List<String>) {
        this.productId = productId
        this.impressions.clear()
        this.impressions.addAll(impressions)
        // If enabled, prints messages via android.util.Log API
        croboxInstance.enableLogging()

        testClickEvent()

        testPageViewEvent()

        testAddToCartEvent()

        testRemoveFromCartEvent()

        testSendErrorEvent()

        testCheckOutEvent()

        testPurchaseEvent()

        testGetPromotions()

        // Disable error logging
        croboxInstance.disableLogging()
    }

    var _overviewPage: RequestQueryParams? = null
    var _cartPage: RequestQueryParams? = null

    private fun overviewPage(): RequestQueryParams {
        if(_overviewPage != null) return _overviewPage!!
        // RequestQueryParams contains page specific parameters, shared by all requests fired from the same page/view.
        _overviewPage = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageOverview
        )

        return _overviewPage!!;
    }

    private fun getCartPage(): RequestQueryParams {
        if(_cartPage != null) return _cartPage!!
        _cartPage = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageCart
        )

        return _cartPage!!
    }

    private fun testClickEvent() {
        // Sending Click events
        croboxInstance.clickEvent(
            overviewPage(),
            clickQueryParams = ClickQueryParams(
                productId = productId,
                price = 1.0,
                quantity = 1
            )
        )
    }

    private fun testPageViewEvent() {
        // The moment user visits a page/view, eg. CartPage, new request params must be created
        val indexPageParams = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageIndex
        )

        // Sending Page View events
        croboxInstance.pageViewEvent(
            indexPageParams,
            PageViewParams(
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

    private fun testAddToCartEvent() {
        // Sending AddToCart events
        croboxInstance.addToCartEvent(
            overviewPage(),
            cartQueryParams = CartQueryParams(
                productId = productId,
                price = 1.0,
                quantity = 1
            )
        )
    }

    private fun testRemoveFromCartEvent() {
        // Sending Remove From Cart events
        croboxInstance.removeFromCartEvent(
            getCartPage(),
            cartQueryParams = CartQueryParams(
                productId = productId,
                price = 1.0,
                quantity = 1
            )
        )
    }

    private fun testSendErrorEvent() {
        // Sending Error events
        croboxInstance.errorEvent(
            getCartPage(),
            errorQueryParams = ErrorQueryParams(
                tag = "ParsingError",
                name = "IllegalArgumentException",
                message = "bad input",
                file = "DemoActivity",
                line = 100
            )
        )
    }

    private fun testCheckOutEvent() {
        // Sending Checkout events
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

    private fun testPurchaseEvent() {
        // Sending Purchase Events
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

    private fun testGetPromotions() {
        val detailPageParams = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageDetail
        )
        // A stub implementation for promotion response handling
        val stubPromotionCallback = object : PromotionCallback {
            val TAG = "PromotionCallback"

            override fun onPromotions(response: PromotionsResponse) {
                printPromotionResponse(response)
            }

            override fun onError(msg: String) {
                Log.d(TAG, "Promotion failed with $msg")
            }
        }

        // Requesting for a promotion from an overview Page with placeholderId configured for Overview Pages in Crobox Container
        croboxInstance.promotions(
            placeholderId = 1,
            queryParams = overviewPage(),
            impressions = impressions,
            promotionCallback = stubPromotionCallback
        )

        // Requesting for a promotion from a product detail page with another placeholderId for a single product
        croboxInstance.promotions(
            placeholderId = 2,
            queryParams = detailPageParams,
            impressions = impressions.subList(0, 1),
            promotionCallback = stubPromotionCallback
        )

        // Requesting for a promotion from another page with another placeholderId without any product
        croboxInstance.promotions(
            placeholderId = 1,
            queryParams = detailPageParams,
            promotionCallback = stubPromotionCallback
        )

        // Disable error logging
        croboxInstance.disableLogging()

        Thread.sleep(2000)
    }

    private fun printPromotionResponse(response: PromotionsResponse) {
        val context = response.context
        val promotions = response.promotions

        val visitorId = context.visitorId
        val sessionId = context.sessionId
        val campaigns = context.campaigns.map { campaign ->
            "Campaign[Id: ${campaign.id}, Name: ${campaign.name}]"
        }
        val contextStr =
            """
                Context [
                    VisitorId: $visitorId, 
                    SessionId: $sessionId
                    Campaigns: ${campaigns.joinToString()}                
                ]        
            """.trimIndent()

        val promotionsStr = promotions.map { promotion ->
            val promotionId = promotion.id
            val campaignId = promotion.campaignId
            val variantId = promotion.variantId
            val productId = promotion.productId ?: ""
            val configStr = promotion.content?.contentConfig()?.let {
                when (it) {
                    is SecondaryMessaging ->
                        """
                            Secondary Messaging Text: ${it.text}
                            Secondary Messaging Font Color:${it.fontColor}"
                            Secondary Messaging Font Android Color:${it.fontColorAndroid()}"
                        """.trimIndent()

                    is TextBadge ->
                        """
                            Text: ${it.text}
                            Font Color: ${it.fontColor}
                            Border Color: ${it.borderColor}
                            Background Color: ${it.backgroundColor}"
                            Font Android Color: ${it.fontColorAndroid()}
                            Border Android Color: ${it.borderColorAndroid()}
                            Background Android Color: ${it.backgroundColorAndroid()}"
                        """.trimIndent()

                    is ImageBadge ->
                        """
                            Image: ${it.image}
                            AltText: ${it.altText}
                        """.trimIndent()

                    else -> "unknown"
                }
            }

            """
                Promotion[
                    Id:$promotionId
                    Product:$productId
                    Campaign:$campaignId
                    Variant:$variantId
                    Msg Id:${promotion.content?.messageId}
                    Component:${promotion.content?.componentName}
                    Msg Config:${promotion.content?.config}
                    Content Config: $configStr
                ]
            """.trimIndent()
        }

        Log.d(
            "PromotionCallback", """
                context: $contextStr,
                promotions: ${promotionsStr.joinToString()}
                """.trimIndent()
        )

    }
}