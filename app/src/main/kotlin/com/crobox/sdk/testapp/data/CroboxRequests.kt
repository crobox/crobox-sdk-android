package com.crobox.sdk.testapp.data

import android.util.Log
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
import com.crobox.sdk.domain.ImageBadge
import com.crobox.sdk.domain.PromotionsResponse
import com.crobox.sdk.domain.SecondaryMessaging
import com.crobox.sdk.domain.TextBadge
import com.crobox.sdk.presenter.PromotionCallback
import com.crobox.sdk.testapp.data.model.Product
import com.crobox.sdk.testapp.data.model.PurchaseItem
import java.util.UUID

class CroboxRequests(val croboxInstance: Crobox) {

    private var productIds = mutableListOf<String>()

    var _overviewPage: ViewContext? = null
    var _cartPage: ViewContext? = null

    private fun overviewPage(): ViewContext {
        if(_overviewPage != null) return _overviewPage!!
        // ViewContext contains view specific parameters, shared by all requests fired from the same view.
        _overviewPage = ViewContext(
            viewId = UUID.randomUUID(),
            viewType = ViewType.Overview
        )

        return _overviewPage!!;
    }

    private fun getCartPage(): ViewContext {
        if(_cartPage != null) return _cartPage!!
        _cartPage = ViewContext(
            viewId = UUID.randomUUID(),
            viewType = ViewType.Cart
        )

        return _cartPage!!
    }

    fun clickEvent(product: Product) {
        // Sending Click events
        croboxInstance.clickEvent(
            overviewPage(),
            params = ClickParams(
                productId = product.id.toString(),
                quantity = 1
            )
        )
    }

    fun viewEvent(viewName: String) {
        // The moment user visits a view, eg. the cart, a new ViewContext must be created
        val indexView = ViewContext(
            viewId = UUID.randomUUID(),
            viewType = ViewType.Index
        )

        // Sending View events
        croboxInstance.viewEvent(
            indexView,
            ViewParams(
                title = viewName,
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

    fun addToCartEvent(item: PurchaseItem) {
        // Sending AddToCart events
        croboxInstance.addToCartEvent(
            overviewPage(),
            params = CartParams(
                productId = item.product.id.toString(),
                quantity = item.quantity
            )
        )
    }

    fun removeFromCartEvent(item: PurchaseItem) {
        // Sending Remove From Cart events
        croboxInstance.removeFromCartEvent(
            getCartPage(),
            params = CartParams(
                productId = item.product.id.toString(),
                quantity = item.quantity
            )
        )
    }

    fun sendErrorEvent() {
        // Sending Error events
        croboxInstance.errorEvent(
            getCartPage(),
            params = ErrorParams(
                tag = "ParsingError",
                name = "IllegalArgumentException",
                message = "bad input",
                file = "DemoActivity",
                line = 100
            )
        )
    }

    private fun purchaseItemsToProductItems(items: MutableList<PurchaseItem>): List<ProductParams> {
        return items.map { item ->
            ProductParams(
                productId = item.product.id.toString(),
                quantity = item.quantity
            )
        }
    }

    fun checkOutEvent(items: MutableList<PurchaseItem>) {
        // Sending Checkout events
        val checkoutView = ViewContext(
            viewId = UUID.randomUUID(),
            viewType = ViewType.Checkout
        )
        // Sending Page View events
        val productParams = purchaseItemsToProductItems(items)
        croboxInstance.checkoutEvent(
            checkoutView,
            CheckoutParams(
                products = productParams,
                step = "1",
                customProperties = mapOf(Pair("page-specific", "true"))
            )
        )
    }

    fun purchaseEvent(items: MutableList<PurchaseItem>) {
        // Sending Purchase Events
        val completeView = ViewContext(
            viewId = UUID.randomUUID(),
            viewType = ViewType.Complete
        )
        // Sending Page View events
        val productParams = purchaseItemsToProductItems(items)
        croboxInstance.purchaseEvent(
            completeView,
            PurchaseParams(
                products = productParams,
                transactionId = "abc123",
                affiliation = "google store",
                coupon = "some coupon",
                revenue = 5.0,
                customProperties = mapOf(Pair("page-specific", "true"))
            )
        )
    }

    fun getPromotions() {
        val detailView = ViewContext(
            viewId = UUID.randomUUID(),
            viewType = ViewType.Detail
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

        // Requesting for a promotion from an overview Page with a placementId configured for Overview views in Crobox Container
        croboxInstance.promotions(
            placementId = 1,
            view = overviewPage(),
            productIds = productIds,
            callback = stubPromotionCallback
        )

        // Requesting for a promotion from a product detail page with another placementId for a single product
        croboxInstance.promotions(
            placementId = 2,
            view = detailView,
            productIds = productIds.subList(0, 1),
            callback = stubPromotionCallback
        )

        // Requesting for a promotion from another page with another placementId without any product
        croboxInstance.promotions(
            placementId = 1,
            view = detailView,
            callback = stubPromotionCallback
        )

        // Disable error logging
        croboxInstance.isLoggingEnabled = false

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
            val configStr = promotion.content?.component()?.let {
                when (it) {
                    is SecondaryMessaging ->
                        """
                            Secondary Messaging Text: ${it.text}
                            Secondary Messaging Font Color: ${it.fontColor?.let { color -> "#%08x".format(color) }}
                        """.trimIndent()

                    is TextBadge ->
                        """
                            Text: ${it.text}
                            Font Color: ${it.fontColor?.let { color -> "#%08x".format(color) }}
                            Border Color: ${it.borderColor?.let { color -> "#%08x".format(color) }}
                            Background: ${it.backgroundDrawable()}
                        """.trimIndent()

                    is ImageBadge ->
                        """
                            Image URL: ${it.imageUrl}
                            AltText: ${it.altText}
                        """.trimIndent()
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

    fun setProductIds(productIds: List<String>) {
        this.productIds = productIds.toMutableList()
    }
}