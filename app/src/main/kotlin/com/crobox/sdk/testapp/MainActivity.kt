package com.crobox.sdk.testapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.crobox.sdk.common.CurrencyCode
import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.config.CroboxConfig
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
    private val containerId = "4ekjfx"

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
                productId = "0001ABC",
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
                productId = "001ABC",
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
                productId = "001ABC",
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
                Log.i("TestApp", "Received error")
                Log.d("PromotionView onError", "" + msg);
            }
        }
        Log.i("TestApp", "Sending Requests:")
        // Requesting for a promotion from an overview Page
        // with placeholderId configured for Overview Pages in Crobox Container
        // for a collection of products/impressions
//        val impressions: List<String> = listOf("001ABC", "002DEF")
        val impressions: List<String> = listOf(
            "37694112",
            "37922101",
            "37933001",
            "T8013162",
            "D6012102",
            "V5957100",
            "Q1759104",
            "V5104004",
            "D5855001",
            "T8532031",
            "F9479100",
            "D0587308",
            "N7405100",
            "Q1759100",
            "Q1759006",
            "F0721900",
            "Z0790003",
            "J4464001",
            "Q8992101",
            "Q1759106",
            "Z4178010",
            "F4146101",
            "N4180162",
            "R8785800",
            "R8784500",
            "F4830203",
            "D5485160",
            "N4180041",
            "C4933001",
            "HJ983100",
            "W0784108",
            "J6015006",
            "Z5485010",
            "D5423003",
            "Z4627800",
            "ID8896",
            "V1290100",
            "J9431002",
            "N4180116",
            "D5485130",
            "V2917803",
            "C0790002",
            "D0383161",
            "Z7885500",
            "Z5485042",
            "M3711144",
            "BB480LEA",
            "M1123005"
        )

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
            impressions = listOf("D0587308"),
            promotionCallback = stubPromotionCallback
        )

        // Requesting for a promotion from another page with another placeholderId without any product
        croboxInstance.promotions(
            placeholderId = 1,
            queryParams = detailPageParams,
            promotionCallback = stubPromotionCallback
        )

        // Disable debugging
        croboxInstance.disableLogging()

        Thread.sleep(2000)
    }
}