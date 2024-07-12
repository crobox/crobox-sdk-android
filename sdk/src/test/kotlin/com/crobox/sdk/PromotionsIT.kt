package com.crobox.sdk

import com.crobox.sdk.common.CurrencyCode
import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.data.model.PageType
import com.crobox.sdk.data.model.RequestQueryParams
import com.crobox.sdk.domain.Campaign
import com.crobox.sdk.domain.Promotion
import com.crobox.sdk.domain.PromotionContent
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
            val context = response?.context
            val visitorId = response?.context?.visitorId
            val sessionId = response?.context?.sessionId
            val groupName = response?.context?.groupName
            response?.context?.campaigns?.let { campaigns: List<Campaign> ->
                for (campaign in campaigns) {
                    val campaignId = campaign.id
                    val campaignName = campaign.name
                    val variantId = campaign.variantId
                    val variantName = campaign.variantName
                    val control = campaign.control
                }
            }

            response?.promotions?.let { promotions: List<Promotion> ->
                for (promotion in promotions) {
                    val promotionId = promotion.id
                    val campaignId = promotion.campaignId
                    val variantId = promotion.variantId
                    promotion.content?.let { content: PromotionContent ->
                        val messageId = content.id
                        val componentName = content.component
                        val config = content.config

                    }

                }
            }
        }

        override fun onError(msg: String?) {

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