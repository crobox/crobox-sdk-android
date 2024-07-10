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

    private val containerId = "4ekjfx"

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

    private val impressions: List<String> = listOf(
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
            impressions = listOf("D0587308"),
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