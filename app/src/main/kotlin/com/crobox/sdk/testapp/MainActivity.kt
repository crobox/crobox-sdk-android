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
import com.crobox.sdk.domain.PromotionsResponse
import com.crobox.sdk.presenter.PromotionView
import com.crobox.sdk.presenter.EventView

class MainActivity : AppCompatActivity() {

    var croboxInstance = Crobox.getInstance(
        CroboxConfig(
            containerId = "xlhvci",
            visitorId = "H9O1I0kYSaekKFrzS_JWCg",
            currencyCode = "USD",
            localeCode = LocaleCode.EN_US
        )
    )

    override fun onActionModeStarted(mode: ActionMode?) {
        super.onActionModeStarted(mode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        croboxInstance.enableDebug()

        // Create an instance of RequestQueryParams with some example data
        val queryParams = RequestQueryParams(
            viewCounter = 28,
            viewId = "An0N-dq0ThWeiUJX12cpVA",
            pageType = PageType.PageDetail
        )

        // Implement the PromotionView interface
        val promotionView = object : PromotionView {
            override fun onPromotions(response: PromotionsResponse?) {
                Log.d("PromotionView", " onPromotions " + response?.context?.pid);
            }

            override fun onError(msg: String?) {
                Log.d("PromotionView onError", "" + msg);
            }
        }
        croboxInstance.promotions(promotionView = promotionView, placeholderId = "14", queryParams = queryParams)


        //addToCart event test
        val eventView = object : EventView {

            override fun onSuccess(dictionary: Map<String, String>) {
                super.onSuccess(dictionary)
                println("EventView onSuccess: $dictionary")
            }

            override fun onError(msg: String?) {
                Log.d("EventView onError", "" + msg);
            }
        }

        // AddCart event with addCartQueryParams
        val addCartQueryParams = AddCartQueryParams(productId = "", category = "", price = 1.0, quantity = 2)
        croboxInstance.addToCartEvent(queryParams, addCartQueryParams = addCartQueryParams, eventView = eventView)

        croboxInstance.disableDebug()

    }
}