package com.crobox.sdk.testapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.crobox.sdk.Common.LocaleCode
import com.crobox.sdk.Core.Crobox
import com.crobox.sdk.Data.Model.AddCartQueryParams
import com.crobox.sdk.Data.Model.PageType
import com.crobox.sdk.Data.Model.RequestQueryParams
import com.crobox.sdk.Domain.PromotionsResponse
import com.crobox.sdk.Presenter.PromotionView
import com.crobox.sdk.Presenter.SocketView

class MainActivity : AppCompatActivity() {

    var croboxInstance = Crobox.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        croboxInstance.isDebug = true

        croboxInstance.containerId = "xlhvci"

        // Create an instance of RequestQueryParams with some example data
        val queryParams = RequestQueryParams(
            viewCounter = 28,
            viewId = "An0N-dq0ThWeiUJX12cpVA",
            visitorId = "H9O1I0kYSaekKFrzS_JWCg",
            currencyCode = "USD",
            localeCode = LocaleCode.EN_US,
            pageType = PageType.PageDetail
        )

        // Implement the PromotionView interface
        val promotionView = object : PromotionView {
            override fun onPromotions(response: PromotionsResponse?) {
              Log.d("PromotionView"," onPromotions "+ response?.context?.pid);
            }
            override fun onError(msg: String?) {
                Log.d("PromotionView onError",""+ msg);
            }
        }
        croboxInstance.promotions( promotionView = promotionView, placeholderId = "14", queryParams = queryParams)



        //socket event test
        val socketView = object : SocketView {

            override fun onSocketSuccess(dictionary: Map<String, String>) {
                super.onSocketSuccess(dictionary)
                println("SocketView onSuccess: $dictionary")
            }
            override fun onError(msg: String?) {
                Log.d("SocketView onError",""+ msg);
            }
        }

        // AddCart event with addCartQueryParams
        val addCartQueryParams = AddCartQueryParams(productId = "", category = "", price = 1.0, quantity = 2)
        croboxInstance.pageViewAddCart(queryParams, addCartQueryParams = addCartQueryParams, socketView = socketView)
    }
}