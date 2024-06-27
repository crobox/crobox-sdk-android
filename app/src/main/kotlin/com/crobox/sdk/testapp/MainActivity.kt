package com.crobox.sdk.testapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.data.model.AddCartQueryParams
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.data.model.ClickQueryParams
import com.crobox.sdk.data.model.PageType
import com.crobox.sdk.data.model.RequestQueryParams
import com.crobox.sdk.presenter.EventCallback
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

        // Create an instance of RequestQueryParams with data specific to this viewing
        val queryParams = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageDetail
        )

        //Sending Click events
        croboxInstance.clickEvent(
            queryParams,
            clickQueryParams = ClickQueryParams(
                productId = "0001ABC",
                price = 1.0,
                quantity = 1
            ),
            eventCallback = object : EventCallback {

            override fun onSuccess(dictionary: Map<String, String>) {
                super.onSuccess(dictionary)
                Log.d("EventView onSuccess", dictionary.toString())
            }

            override fun onError(msg: String?) {
                Log.d("EventView onError", "" + msg);
            }
        })


        //Sending AddToCart events
        croboxInstance.addToCartEvent(
            queryParams,
            addCartQueryParams = AddCartQueryParams(
                productId = "",
                category = "",
                price = 1.0,
                quantity = 2
            ),
            eventCallback = object : EventCallback {

                override fun onSuccess(dictionary: Map<String, String>) {
                    super.onSuccess(dictionary)
                    Log.d("EventView onSuccess", dictionary.toString())
                }

                override fun onError(msg: String?) {
                    Log.d("EventView onError", "" + msg);
                }
            })


        // Implement the PromotionView interface
//        val promotionView = object : PromotionView {
//            override fun onPromotions(response: PromotionsResponse?) {
//                Log.d("PromotionView", " onPromotions " + response?.context?.pid);
//            }
//
//            override fun onError(msg: String?) {
//                Log.d("PromotionView onError", "" + msg);
//            }
//        }
//        croboxInstance.promotions(promotionView = promotionView, placeholderId = "14", queryParams = queryParams)


        croboxInstance.disableDebug()

    }
}