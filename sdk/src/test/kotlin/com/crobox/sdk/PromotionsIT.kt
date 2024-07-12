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
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Ignore
class PromotionsIT {

    object Conf {
        val vid: UUID = UUID.randomUUID()
        val pid: UUID = UUID.randomUUID()
    }

    // FL TEST
    private val containerId = "xlrc9t"
    private val placeholderId = 1
    private val placeholderId2 = 2

    private val croboxInstance = Crobox.getInstance(
        CroboxConfig(
            containerId = containerId,
            visitorId = Conf.pid,
            currencyCode = CurrencyCode.USD,
            localeCode = LocaleCode.EN_US
        )
    )

    private val overviewPageParams = RequestQueryParams(
        viewId = Conf.vid,
        pageType = PageType.PageOverview
    )

    private val detailPageParams = RequestQueryParams(
        viewId = UUID.randomUUID(),
        pageType = PageType.PageDetail
    )

    private val impressions: List<String> =
        listOf("product1", "product2", "product3", "product4", "product5")

    @After
    fun after() {
        Thread.sleep(2000)
    }

    @Test
    fun skipped_testMultipleProducts() {
        val lock = CountDownLatch(1);
        var visitorId: UUID? = null
        var sessionId: UUID? = null
        croboxInstance.promotions(
            placeholderId = placeholderId,
            queryParams = overviewPageParams,
            impressions = impressions,
            promotionCallback = object : PromotionCallback {
                override fun onPromotions(response: PromotionsResponse) {
                    println(response)
                    visitorId = response.context.visitorId
                    sessionId = response.context.sessionId
                    lock.countDown()
                }

                override fun onError(msg: String) {
                    println("Error:$msg")
                    lock.countDown()
                }
            }
        )
        lock.await(2, TimeUnit.SECONDS)
        Assert.assertEquals(Conf.pid, visitorId)
        Assert.assertNotNull(sessionId)
    }

    @Test
    fun skipped_testOneProduct() {
        val lock = CountDownLatch(1);
        var visitorId: UUID? = null
        var sessionId: UUID? = null
        croboxInstance.promotions(
            placeholderId = placeholderId2,
            queryParams = detailPageParams,
            impressions = impressions.subList(0, 1),
            promotionCallback = object : PromotionCallback {
                override fun onPromotions(response: PromotionsResponse) {
                    println(response)
                    visitorId = response.context.visitorId
                    sessionId = response.context.sessionId
                    lock.countDown()
                }

                override fun onError(msg: String) {
                    println("Error:$msg")
                    lock.countDown()
                }
            }
        )
        lock.await(2, TimeUnit.SECONDS)
        Assert.assertEquals(Conf.pid, visitorId)
        Assert.assertNotNull(sessionId)
    }

    @Test
    fun skipped_testNoProduct() {
        val lock = CountDownLatch(1);
        var visitorId: UUID? = null
        var sessionId: UUID? = null
        croboxInstance.promotions(
            placeholderId = placeholderId,
            queryParams = overviewPageParams,
            promotionCallback = object : PromotionCallback {
                override fun onPromotions(response: PromotionsResponse) {
                    println(response)
                    visitorId = response.context.visitorId
                    sessionId = response.context.sessionId
                    lock.countDown()
                }

                override fun onError(msg: String) {
                    println("Error:$msg")
                    lock.countDown()
                }
            }
        )
        lock.await(2, TimeUnit.SECONDS)
        Assert.assertEquals(Conf.pid, visitorId)
        Assert.assertNotNull(sessionId)
    }

}