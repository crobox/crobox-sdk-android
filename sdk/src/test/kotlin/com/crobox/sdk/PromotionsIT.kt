package com.crobox.sdk

import com.crobox.sdk.common.CurrencyCode
import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.data.model.PageType
import com.crobox.sdk.data.model.RequestQueryParams
import com.crobox.sdk.domain.PromotionsResponse
import com.crobox.sdk.presenter.PromotionCallback
import org.junit.After
import org.junit.Test
import java.util.UUID

class PromotionsIT {

    private val vid = UUID.randomUUID()
    private val pid = UUID.randomUUID()

    // FL TEST
    private val containerId = "xlrc9t"
    private val placeholderId = 1
    private val placeholderId2 = 2

    private val croboxInstance = Crobox.getInstance(
        CroboxConfig(
            containerId = containerId,
            visitorId = pid,
            currencyCode = CurrencyCode.USD,
            localeCode = LocaleCode.EN_US
        )
    )

    private val overviewPageParams = RequestQueryParams(
        viewId = vid,
        pageType = PageType.PageOverview
    )

    private val detailPageParams = RequestQueryParams(
        viewId = UUID.randomUUID(),
        pageType = PageType.PageDetail
    )

    private val impressions: List<String> =
        listOf("product1", "product2", "product3", "product4", "product5")

    private val stubPromotionCallback = object : PromotionCallback {
        override fun onPromotions(response: PromotionsResponse?) {
            val contextStr =
                """
                Context.VisitorId: ${response?.context?.visitorId}
                Context.SessionId: ${response?.context?.sessionId}
                Context.Campaigns: ${
                    response?.context?.campaigns?.map { campaign ->
                        "[Id: ${campaign.id}, Name: ${campaign.name}]"
                    }?.joinToString(",")
                }                
                """.trimIndent()

            val promotionsStr = response?.promotions?.map { promotion ->
                """
                    Promotion[
                        Id:${promotion.id}
                        Product:${promotion.productId}
                        Campaign:${promotion.campaignId}
                        Variant:${promotion.variantId}
                        Msg Id:${promotion.content?.id}
                        Msg Component:${promotion.content?.component}
                        Msg Config:${promotion.content?.config}
                    ]
                """.trimIndent()
            }?.joinToString(",")

            println("$contextStr\n$promotionsStr")
        }

        override fun onError(msg: String?) {
            println("Promotion failed with $msg")
        }
    }

    @After
    fun after() {
        Thread.sleep(2000)
    }

    @Test
    fun testMultipleProducts() {
        croboxInstance.promotions(
            placeholderId = placeholderId,
            queryParams = overviewPageParams,
            impressions = impressions,
            promotionCallback = stubPromotionCallback
        )
    }

    @Test
    fun testOneProduct() {
        croboxInstance.promotions(
            placeholderId = placeholderId2,
            queryParams = detailPageParams,
            impressions = impressions.subList(0, 1),
            promotionCallback = stubPromotionCallback
        )
    }

    @Test
    fun testNoProduct() {
        croboxInstance.promotions(
            placeholderId = 1,
            queryParams = detailPageParams,
            promotionCallback = stubPromotionCallback
        )
    }

}