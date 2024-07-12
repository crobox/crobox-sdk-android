package com.crobox.sdk.testapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.crobox.sdk.common.CurrencyCode
import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.data.model.CartQueryParams
import com.crobox.sdk.data.model.ClickQueryParams
import com.crobox.sdk.data.model.ErrorQueryParams
import com.crobox.sdk.data.model.PageType
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
    private val productId = impressions.get(0)

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
        croboxInstance.pageViewEvent(indexPageParams)

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
                            Component:${promotion.content?.component}
                            Msg Config:${promotion.content?.config}
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