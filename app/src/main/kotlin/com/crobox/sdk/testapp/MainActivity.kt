package com.crobox.sdk.testapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import com.crobox.sdk.domain.PromotionsResponse
import com.crobox.sdk.presenter.PromotionCallback
import java.util.UUID

class MainActivity : AppCompatActivity() {

    // Use the Container ID assigned by Crobox
    private val containerId = "xlrc9t"

    // Collection of products/impressions
    private val impressions: List<String> =
        listOf("product1", "product2", "product3", "product4", "product5")
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // If enabled, prints messages via android.util.Log API
        croboxInstance.enableLogging()

        // RequestQueryParams contains page specific parameters, shared by all requests fired from the same page/view.
        val overviewPageParams = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageOverview
        )

        // Sending Click events
        croboxInstance.clickEvent(
            overviewPageParams,
            clickQueryParams = ClickQueryParams(
                productId = productId,
                price = 1.0,
                quantity = 1
            )
        )

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

        val detailPageParams = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageDetail
        )

        // Sending AddToCart events
        croboxInstance.addToCartEvent(
            overviewPageParams,
            cartQueryParams = CartQueryParams(
                productId = productId,
                price = 1.0,
                quantity = 1
            )
        )

        val cartPageParams = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageCart
        )

        // Sending Remove From Cart events
        croboxInstance.removeFromCartEvent(
            cartPageParams,
            cartQueryParams = CartQueryParams(
                productId = productId,
                price = 1.0,
                quantity = 1
            )
        )

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


        // A stub implementation for promotion response handling
        val stubPromotionCallback = object : PromotionCallback {
            val TAG = "PromotionCallback"

            override fun onPromotions(response: PromotionsResponse) {
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

                    """
                        Promotion[
                            Id:$promotionId
                            Product:$productId
                            Campaign:$campaignId
                            Variant:$variantId
                            Msg Id:${promotion.content?.messageId}
                            Component:${promotion.content?.componentName}
                            Msg Config:${promotion.content?.config}
                            Image Badge:${promotion.content?.getImageBadge()}
                            Text Badge:${promotion.content?.getTextBadge()}
                        ]
                    """.trimIndent()
                }

                Log.d(
                    TAG, """
                context: $contextStr,
                promotions: ${promotionsStr.joinToString()}
                """.trimIndent()
                )
            }

            override fun onError(msg: String) {
                Log.d(TAG, "Promotion failed with $msg")
            }
        }

        // Requesting for a promotion from an overview Page with placeholderId configured for Overview Pages in Crobox Container
        croboxInstance.promotions(
            placeholderId = 1,
            queryParams = overviewPageParams,
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
}