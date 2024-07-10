package com.crobox.sdk

import com.crobox.sdk.common.CurrencyCode
import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.data.model.PageType
import com.crobox.sdk.data.model.RequestQueryParams
import com.crobox.sdk.domain.PromotionsResponse
import com.crobox.sdk.presenter.PromotionCallback
import org.junit.After
import org.junit.Test
import java.util.UUID

class PromotionsIT {

    private val containerId = "xlrc9t"

    private val croboxInstance = Crobox.getInstance(
        CroboxConfig(
            containerId = containerId,
            visitorId = UUID.randomUUID(),
            currencyCode = CurrencyCode.USD,
            localeCode = LocaleCode.EN_US
        )
    )

    private val overviewPageParams = RequestQueryParams(
        viewId = UUID.randomUUID(),
        pageType = PageType.PageOverview
    )

    private val detailPageParams = RequestQueryParams(
        viewId = UUID.randomUUID(),
        pageType = PageType.PageDetail
    )

    private val impressions: List<String> = listOf("product1", "product2", "product3", "product4", "product5")

    private val stubPromotionCallback = object : PromotionCallback {
        override fun onPromotions(response: PromotionsResponse?) {
            val experiments: List<String>? =
                response?.context?.experiments?.map { experiment ->
                    "Experiment[Id: ${experiment.id}, Name: ${experiment.name}]"
                }

            println(
                """
                Context [
                    VisitorId: ${response?.context?.visitorId}
                    SessionId: ${response?.context?.sessionId}
                    Experiments: ${experiments?.joinToString()}                
                ]
                """.trimIndent()
            )

            response?.promotions?.forEach { promotion ->
                println(
                    """
                    Promotion[
                        Id:${promotion.id}
                        Product:${promotion.productId}
                        Campaign:${promotion.campaignId}
                        Variant:${promotion.variantId}
                        Msg Id:${promotion.content?.id}
                        Msg Config:${promotion.content?.config}
                    ]
                """.trimIndent()
                )
            }
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
            placeholderId = 1,
            queryParams = overviewPageParams,
            impressions = impressions,
            promotionCallback = stubPromotionCallback
        )
    }

    @Test
    fun testOneProduct() {
        croboxInstance.promotions(
            placeholderId = 2,
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

    @Test
    fun testPromotionError() {
        croboxInstance.promotions(
            placeholderId = 1,
            queryParams = overviewPageParams,
            impressions = impressions,
            promotionCallback = stubPromotionCallback
        )

    }
}