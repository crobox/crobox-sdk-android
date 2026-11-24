package com.crobox.sdk

import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.data.model.ViewContext
import com.crobox.sdk.data.model.ViewType
import com.crobox.sdk.domain.PromotionsResponse
import com.crobox.sdk.presenter.PromotionCallback
import org.junit.After
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Ignore("TODO set up integration container")
class PromotionsIT {

    object Conf {
        val vid: UUID = UUID.randomUUID()
        val pid: UUID = UUID.randomUUID()
    }

    // FL TEST
    private val containerId = "xlrc9t"
    private val placementId = 1
    private val placementId2 = 2

    private val croboxInstance = Crobox.getInstance(
        CroboxConfig(
            containerId = containerId,
            visitorId = Conf.pid,
            localeCode = LocaleCode("en_US")
        )
    )

    private val overviewView = ViewContext(
        viewId = Conf.vid,
        viewType = ViewType.Overview
    )

    private val detailView = ViewContext(
        viewId = UUID.randomUUID(),
        viewType = ViewType.Detail
    )

    private val productIds: List<String> =
        listOf("product1", "product2", "product3", "product4", "product5")

    @After
    fun after() {
        Thread.sleep(2000)
    }

    @Test
    fun testMultipleProducts() {
        val lock = CountDownLatch(1);
        var visitorId: UUID? = null
        var sessionId: UUID? = null
        croboxInstance.promotions(
            placementId = placementId,
            view = overviewView,
            productIds = productIds,
            callback = object : PromotionCallback {
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
    fun testOneProduct() {
        val lock = CountDownLatch(1);
        var visitorId: UUID? = null
        var sessionId: UUID? = null
        croboxInstance.promotions(
            placementId = placementId2,
            view = detailView,
            productIds = productIds.subList(0, 1),
            callback = object : PromotionCallback {
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
    fun testNoProduct() {
        val lock = CountDownLatch(1);
        var visitorId: UUID? = null
        var sessionId: UUID? = null
        croboxInstance.promotions(
            placementId = placementId,
            view = overviewView,
            callback = object : PromotionCallback {
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