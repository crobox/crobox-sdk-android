package com.crobox.sdk.testapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.crobox.sdk.Common.LocaleCode
import com.crobox.sdk.Core.Crobox
import com.crobox.sdk.Data.Model.PageType
import com.crobox.sdk.Data.Model.RequestQueryParams
import com.crobox.sdk.Domian.PromotionsResponse
import com.crobox.sdk.Presenter.PromotionView

class MainActivity : AppCompatActivity() {

    var croboxInstance = Crobox.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Create an instance of RequestQueryParams with some example data
        val requestParams = RequestQueryParams(
            viewCounter = 0,
            viewId = "exampleViewId",
            visitorId = "exampleVisitorId",
            currencyCode = "USD",
            localeCode = LocaleCode.EN_US,
            userId = "exampleUserId",
            timestamp = "lu9znf91",
            timezone = -4,
            pageType = PageType.PageComplete,
            customProperties = mapOf("cp.mobileUser" to "yes"),
            pageUrl = this.localClassName,
            referrerUrl = this.localClassName
        )

        // Implement the PromotionView interface
        val promotionView = object : PromotionView {
            override fun onPromotions(response: PromotionsResponse?) {
                println("Promotions response: $response")
            }
            override fun onError(msg: String?) {
                println("Promotions onError: $msg")
            }
        }

        croboxInstance.promotions(requestParams,promotionView)

    }
}