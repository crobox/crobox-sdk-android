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

    val croboxInstance = Crobox.getInstance(
        CroboxConfig(
            containerId = "xlhvci",
            visitorId = "H9O1I0kYSaekKFrzS_JWCg",
            currencyCode = "USD",
            localeCode = LocaleCode.EN_US
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Enable debugging
        croboxInstance.enableDebug()

        // stub implementation for callbacks
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

        // Sending AddToCart events
        croboxInstance.addToCartEvent(
            overviewPageParams,
            addCartQueryParams = CartQueryParams(
                productId = "001ABC",
                price = 1.0,
                quantity = 1
            ),
            eventCallback = eventCallback
        )

        // When user visits another page, eg. CartPage, new request params must be created
        val cartPageParams = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageCart
        )

        // Sending Remove From Cart events
        croboxInstance.removeFromCartEvent(
            cartPageParams,
            removeFromCartQueryParams = CartQueryParams(
                productId = "001ABC",
                price = 1.0,
                quantity = 1
            ),
            eventCallback = eventCallback
        )

        // Sending Error events from another page
        val indexPageParams = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageIndex
        )
        croboxInstance.errorEvent(
            indexPageParams,
            errorQueryParams = ErrorQueryParams(
                tag = "ParsingError",
                name = "IllegalArgumentException",
                message = "bad input",
                file = "MainActivity",
                line = 100
            ),
            eventCallback = eventCallback
        )

        // Sending View events from another page
        val detailPageParams = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageDetail
        )
        croboxInstance.pageViewEvent(
            detailPageParams,
            eventCallback = eventCallback
        )

        // Requesting for a Promotion
        croboxInstance.promotions(
            placeholderId = "14",
            queryParams = detailPageParams,
            promotionCallback = object : PromotionCallback {
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
            })

        croboxInstance.disableDebug()

    }
}