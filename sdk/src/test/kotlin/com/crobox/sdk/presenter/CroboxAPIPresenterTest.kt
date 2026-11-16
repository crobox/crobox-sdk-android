package com.crobox.sdk.presenter

import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.data.model.EventType
import com.crobox.sdk.data.model.PageType
import com.crobox.sdk.data.model.PageViewParams
import com.crobox.sdk.data.model.ProductParams
import com.crobox.sdk.data.model.PurchaseParams
import com.crobox.sdk.data.model.RequestQueryParams
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
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
        val view = RequestQueryParams(viewId = UUID.randomUUID(), pageType = PageType.PageCheckout)

        val pageview = presenter.eventQuery(view, PageViewParams(), EventType.PageView, null)
        val purchase = presenter.eventQuery(
            view, PurchaseParams(transactionId = "t1"), EventType.Purchase, PageType.PageComplete
        )

        assertEquals(0, pageview["e"])
        assertEquals(1, purchase["e"])
        assertEquals(PageType.PageCheckout.value, pageview["pt"])
        assertEquals(PageType.PageComplete.value, purchase["pt"])
    }

    @Test
    fun impressionsUseTheArrayFormatWithQuantityAndPosition() {
        val view = RequestQueryParams(viewId = UUID.randomUUID(), pageType = PageType.PageComplete)
        val products = linkedSetOf(
            ProductParams(productId = "a", quantity = 2),
            ProductParams(productId = "b")
        )

        val query = presenter.eventQuery(view, PurchaseParams(products = products), EventType.Purchase, null)

        assertEquals("a", query["imp[0].pi"])
        assertEquals(2, query["imp[0].qty"])
        assertEquals(1, query["imp[0].pos"])
        assertEquals("b", query["imp[1].pi"])
        assertEquals(2, query["imp[1].pos"])
        assertFalse(query.containsKey("imp[1].qty"))
        assertFalse(query.containsKey("imp[0]"))
    }

    @Test
    fun indexesPastNineAreKept() {
        val view = RequestQueryParams(viewId = UUID.randomUUID(), pageType = PageType.PageOverview)
        val products = (0 until 12).map { ProductParams(productId = "p$it") }.toSet()

        val query = presenter.eventQuery(view, PageViewParams(impressions = products), EventType.PageView, null)

        assertEquals("p11", query["imp[11].pi"])
        assertEquals(12, query["imp[11].pos"])
    }

    @Test
    fun configCustomPropertiesAreSentAndPagePropertiesWin() {
        val view = RequestQueryParams(
            viewId = UUID.randomUUID(),
            pageType = PageType.PageIndex,
            customProperties = mapOf("shared" to "page")
        )

        val query = presenter.eventQuery(view, null, EventType.Click, null)

        assertEquals("yes", query["cp.app"])
        assertEquals("page", query["cp.shared"])
    }

    @Test
    fun updatedConfigIsUsedByLaterRequests() {
        val presenter = CroboxAPIPresenter(config)
        val view = RequestQueryParams(viewId = UUID.randomUUID(), pageType = PageType.PageIndex)

        presenter.config = config.copy(userId = "JohnDoe", localeCode = LocaleCode("nl_BE"))
        val query = presenter.eventQuery(view, null, EventType.Click, null)

        assertEquals("JohnDoe", query["uid"])
        assertEquals("nl_BE", query["lc"])
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
        val presenter = CroboxAPIPresenter(config.copy(timezone = 3))
        val view = RequestQueryParams(viewId = UUID.randomUUID(), pageType = PageType.PageIndex)

        assertEquals(3, presenter.eventQuery(view, null, EventType.Click, null)["tz"])
    }

    @Test
    fun promotionBodyIsUrlEncoded() {
        val body = presenter.promotionRequestBody(listOf("a&b=c", "100%", "plain"))
        val buffer = Buffer()
        body.writeTo(buffer)

        assertEquals("0=a%26b%3Dc&1=100%25&2=plain", buffer.readUtf8())
    }
}
