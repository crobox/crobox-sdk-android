package com.crobox.sdk.presenter

import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.data.model.CartParams
import com.crobox.sdk.data.model.ClickParams
import com.crobox.sdk.data.model.CustomEventParams
import com.crobox.sdk.data.model.EventType
import com.crobox.sdk.data.model.ProductParams
import com.crobox.sdk.data.model.PurchaseParams
import com.crobox.sdk.data.model.ViewContext
import com.crobox.sdk.data.model.ViewParams
import com.crobox.sdk.data.model.ViewType
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.net.SocketTimeoutException
import java.util.TimeZone
import java.util.UUID

class CroboxAPIPresenterTest {

    private val config = CroboxConfig(
        containerId = "xlrc9t",
        visitorId = UUID.randomUUID(),
        localeCode = LocaleCode("en_US"),
        customProperties = mapOf("app" to "yes", "shared" to "visitor")
    )

    private val presenter = CroboxAPIPresenter(config)

    @Test
    fun purchaseKeepsTheViewCounter() {
        val view = ViewContext(viewId = UUID.randomUUID(), viewType = ViewType.Checkout)

        val pageview = presenter.eventQuery(view, ViewParams(), EventType.PageView, null)
        val purchase = presenter.eventQuery(
            view, PurchaseParams(transactionId = "t1"), EventType.Purchase, ViewType.Complete
        )

        assertEquals(0, pageview["e"])
        assertEquals(1, purchase["e"])
        assertEquals(ViewType.Checkout.value, pageview["pt"])
        assertEquals(ViewType.Complete.value, purchase["pt"])
    }

    @Test
    fun impressionsUseTheArrayFormatWithQuantityAndPosition() {
        val view = ViewContext(viewId = UUID.randomUUID(), viewType = ViewType.Complete)
        val products = listOf(
            ProductParams(productId = "a", quantity = 2, list = "search results"),
            ProductParams(productId = "b")
        )

        val query = presenter.eventQuery(
            view, PurchaseParams(products = products, transactionId = "t1"), EventType.Purchase, null
        )

        assertEquals("a", query["imp[0].pi"])
        assertEquals(2, query["imp[0].qty"])
        assertEquals("search results", query["imp[0].lst"])
        assertEquals(1, query["imp[0].pos"])
        assertEquals("b", query["imp[1].pi"])
        assertEquals(2, query["imp[1].pos"])
        assertFalse(query.containsKey("imp[1].qty"))
        assertFalse(query.containsKey("imp[1].lst"))
        assertFalse(query.containsKey("imp[0]"))
        assertEquals("t1", query["tid"])
    }

    @Test
    fun impressionsKeepOrderAndRepeatedProducts() {
        val view = ViewContext(viewId = UUID.randomUUID(), viewType = ViewType.Overview)
        val products = listOf(ProductParams("b"), ProductParams("a"), ProductParams("b"))

        val query = presenter.eventQuery(view, ViewParams(impressions = products), EventType.PageView, null)

        assertEquals("b", query["imp[0].pi"])
        assertEquals("a", query["imp[1].pi"])
        assertEquals("b", query["imp[2].pi"])
        assertEquals(3, query["imp[2].pos"])
    }

    @Test
    fun productDetailSendsListNameAndNoPrice() {
        val view = ViewContext(viewId = UUID.randomUUID(), viewType = ViewType.Detail)

        val query = presenter.eventQuery(
            view, ViewParams(product = ProductParams("1", quantity = 2, list = "related")), EventType.PageView, null
        )

        assertEquals("1", query["pi"])
        assertEquals(2, query["qty"])
        assertEquals("related", query["lst"])
        assertFalse(query.containsKey("price"))
    }

    @Test
    fun clickCartAndCustomEventsSendNoPrice() {
        val view = ViewContext(viewId = UUID.randomUUID(), viewType = ViewType.Overview)

        listOf(
            presenter.eventQuery(view, ClickParams(productId = "1", quantity = 1), EventType.Click, null),
            presenter.eventQuery(view, CartParams(productId = "1", quantity = 1), EventType.AddCart, null),
            presenter.eventQuery(view, CustomEventParams(name = "x", productId = "1"), EventType.CustomEvent, null)
        ).forEach { query ->
            assertEquals("1", query["pi"])
            assertFalse(query.containsKey("price"))
        }
    }

    @Test
    fun indexesPastNineAreKept() {
        val view = ViewContext(viewId = UUID.randomUUID(), viewType = ViewType.Overview)
        val products = (0 until 12).map { ProductParams(productId = "p$it") }.toSet()

        val query = presenter.eventQuery(view, ViewParams(impressions = products), EventType.PageView, null)

        assertEquals("p11", query["imp[11].pi"])
        assertEquals(12, query["imp[11].pos"])
    }

    @Test
    fun configCustomPropertiesAreSentAndPagePropertiesWin() {
        val view = ViewContext(
            viewId = UUID.randomUUID(),
            viewType = ViewType.Index,
            customProperties = mapOf("shared" to "page")
        )

        val query = presenter.eventQuery(view, null, EventType.Click, null)

        assertEquals("yes", query["cp.app"])
        assertEquals("page", query["cp.shared"])
    }

    @Test
    fun updatedConfigIsUsedByLaterRequests() {
        val presenter = CroboxAPIPresenter(config)
        val view = ViewContext(viewId = UUID.randomUUID(), viewType = ViewType.Index)

        presenter.config = config.copy(userId = "JohnDoe", localeCode = LocaleCode("nl_BE"))
        val query = presenter.eventQuery(view, null, EventType.Click, null)

        assertEquals("JohnDoe", query["uid"])
        assertEquals("nl_BE", query["lc"])
        // The currency comes from the region the locale selects
        assertFalse(query.containsKey("cc"))
    }

    @Test
    fun timezoneIsHoursEastOfUtc() {
        val winter = 1_704_067_200_000L // 2024-01-01T00:00:00Z
        val summer = 1_719_792_000_000L // 2024-07-01T00:00:00Z

        assertEquals(1, presenter.timezoneOffsetHours(TimeZone.getTimeZone("Europe/Amsterdam"), winter))
        assertEquals(2, presenter.timezoneOffsetHours(TimeZone.getTimeZone("Europe/Amsterdam"), summer))
        assertEquals(-5, presenter.timezoneOffsetHours(TimeZone.getTimeZone("America/New_York"), winter))
        assertEquals(5.5, presenter.timezoneOffsetHours(TimeZone.getTimeZone("Asia/Kolkata"), winter))
    }

    @Test
    fun configuredTimezoneWins() {
        val presenter = CroboxAPIPresenter(config.copy(timezone = TimeZone.getTimeZone("Asia/Kolkata")))
        val view = ViewContext(viewId = UUID.randomUUID(), viewType = ViewType.Index)

        assertEquals(5.5, presenter.eventQuery(view, null, EventType.Click, null)["tz"])
    }

    @Test
    fun promotionBodyIsUrlEncoded() {
        val body = presenter.promotionRequestBody(listOf("a&b=c", "100%", "plain"))
        val buffer = Buffer()
        body.writeTo(buffer)

        assertEquals("0=a%26b%3Dc&1=100%25&2=plain", buffer.readUtf8())
    }

    @Test
    fun failureIsReportedAsSdkErrorEvent() {
        val view = ViewContext(viewId = UUID.randomUUID(), viewType = ViewType.Index)
        val query = presenter.eventQuery(view, presenter.failureReport("purchase", "HTTP 400"), EventType.Error, null)

        assertEquals("error", query["t"])
        assertEquals("sdk", query["tg"])
        assertEquals("purchase", query["nm"])
        assertEquals("HTTP 400", query["msg"])
    }

    @Test
    fun failureMessageHasTheExceptionClassAndMessage() {
        assertEquals("SocketTimeoutException: timeout", presenter.failureMessage(SocketTimeoutException("timeout")))
        assertEquals("SocketTimeoutException", presenter.failureMessage(SocketTimeoutException()))
    }

    @Test
    fun checkoutAndPurchaseFailuresAreNamedApart() {
        with(presenter) {
            assertEquals("checkout", EventType.Checkout.requestName)
            assertEquals("purchase", EventType.Purchase.requestName)
            assertNotEquals(EventType.PageView.requestName, EventType.Purchase.requestName)
        }
    }
}
