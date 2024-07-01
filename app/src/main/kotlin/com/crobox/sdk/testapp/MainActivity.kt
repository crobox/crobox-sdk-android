package com.crobox.sdk.testapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.data.model.CartQueryParams
import com.crobox.sdk.data.model.ClickQueryParams
import com.crobox.sdk.data.model.ErrorQueryParams
import com.crobox.sdk.data.model.PageType
import com.crobox.sdk.data.model.RequestQueryParams
import com.crobox.sdk.domain.PromotionsResponse
import com.crobox.sdk.presenter.EventCallback
import com.crobox.sdk.presenter.PromotionCallback
import java.util.UUID

class MainActivity : AppCompatActivity() {

    // CroboxInstance is the single point of all interactions, keeping the configuration and providing all functionality
    val croboxInstance = Crobox.getInstance(
        CroboxConfig(
            containerId = "xlrc9t",
            visitorId = UUID.randomUUID(),
            currencyCode = "USD",
            localeCode = LocaleCode.EN_US
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Enable debugging
        croboxInstance.enableDebug()

        // stub implementation for event callbacks
        val eventCallback = object : EventCallback {

            override fun onSuccess(dictionary: Map<String, String>) {
                super.onSuccess(dictionary)
                Log.d("EventView onSuccess", dictionary.toString())
            }

            override fun onError(msg: String?) {
                Log.d("EventView onError", "" + msg);
            }
        }

        // RequestQueryParams contains page specific parameters, shared by all requests fired from the same page/view.
        val overviewPageParams = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageOverview
        )

        // Sending Click events
        croboxInstance.clickEvent(
            overviewPageParams,
            clickQueryParams = ClickQueryParams(
                productId = "0001ABC",
                price = 1.0,
                quantity = 1
            ),
            eventCallback = eventCallback
        )

        // The moment user visits a page/view, eg. CartPage, new request params must be created
        val indexPageParams = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageIndex
        )

        // Sending Page View events
        croboxInstance.pageViewEvent(
            indexPageParams,
            eventCallback = eventCallback
        )

        val detailPageParams = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageDetail
        )

        // Sending AddToCart events
        croboxInstance.addToCartEvent(
            overviewPageParams,
            cartQueryParams = CartQueryParams(
                productId = "001ABC",
                price = 1.0,
                quantity = 1
            ),
            eventCallback = eventCallback
        )

        val cartPageParams = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageCart
        )

        // Sending Remove From Cart events
        croboxInstance.removeFromCartEvent(
            cartPageParams,
            cartQueryParams = CartQueryParams(
                productId = "001ABC",
                price = 1.0,
                quantity = 1
            ),
            eventCallback = eventCallback
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
            ),
            eventCallback = eventCallback
        )

        // Retrieving Promotions

        val stubPromotionCallback = object : PromotionCallback {
            override fun onPromotions(response: PromotionsResponse?) {
                response?.context?.experiments?.forEach { experiment ->
                    Log.d("PromotionView", "experiment : " + experiment.id);
                }
                response?.promotions?.forEach { promotion ->
                    Log.d("PromotionView", "promotion : " + promotion.productId);
                }

            }

            override fun onError(msg: String?) {
                Log.d("PromotionView onError", "" + msg);
            }
        }

        // Requesting for a promotion from an overview Page
        // with placeholderId configured for Overview Pages in Crobox Container
        // for a collection of products/impressions
        val impressions: List<String> = listOf("001ABC", "002DEF")
        croboxInstance.promotions(
            placeholderId = 1,
            queryParams = overviewPageParams,
            impressions = impressions,
            promotionCallback = stubPromotionCallback
        )

        // Requesting for a promotion from a product detail page with another placeholderId for a singe product
        croboxInstance.promotions(
            placeholderId = 2,
            queryParams = detailPageParams,
            impressions = listOf("001ABC"),
            promotionCallback = stubPromotionCallback
        )

        // Disable debugging
        croboxInstance.disableDebug()

    }
}