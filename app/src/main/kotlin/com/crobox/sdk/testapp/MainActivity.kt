package com.crobox.sdk.testapp

import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import androidx.appcompat.app.AppCompatActivity
import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.data.model.AddCartQueryParams
import com.crobox.sdk.config.CroboxConfig
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
            viewCounter = 28,
            viewId = UUID.randomUUID(),
            pageType = PageType.PageDetail
        )

        //Sending Click event
        val clickEventCallback = object : EventCallback {

            override fun onSuccess(dictionary: Map<String, String>) {
                super.onSuccess(dictionary)
                Log.d("EventView onSuccess", dictionary.toString())
            }

            override fun onError(msg: String?) {
                Log.d("EventView onError", "" + msg);
            }
        }
        croboxInstance.clickEvent(queryParams, eventCallback = clickEventCallback)


//        //Sending addToCart event
//        val eventCallback = object : EventCallback {
//
//            override fun onSuccess(dictionary: Map<String, String>) {
//                super.onSuccess(dictionary)
//                Log.d("EventView onSuccess", dictionary.toString())
//            }
//
//            override fun onError(msg: String?) {
//                Log.d("EventView onError", "" + msg);
//            }
//        }
//
//        // AddCart event with addCartQueryParams
//        val addCartQueryParams = AddCartQueryParams(productId = "", category = "", price = 1.0, quantity = 2)
//        croboxInstance.addToCartEvent(queryParams, addCartQueryParams = addCartQueryParams, eventCallback = eventCallback)


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