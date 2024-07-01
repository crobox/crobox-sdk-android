package com.crobox.sdk.core

import com.crobox.sdk.common.CroboxDebug
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.data.model.*
import com.crobox.sdk.presenter.CroboxAPIPresenter
import com.crobox.sdk.presenter.EventCallback
import com.crobox.sdk.presenter.PromotionCallback

class Crobox private constructor(config: CroboxConfig) {

    private val presenter = CroboxAPIPresenter(config)

    companion object {

        @Volatile
        private var instance: Crobox? = null

        fun getInstance(config: CroboxConfig): Crobox =
            instance ?: synchronized(this) {
                instance ?: Crobox(config).also { instance = it }
            }
    }

    fun enableDebug() {
        CroboxDebug.isDebug = true
    }

    fun disableDebug() {
        CroboxDebug.isDebug = false
    }


    /**
     * For retrieval of Promotions
     *
     * A Placeholder represent a predesignated point on the user interface, where the promotion will be located and displayed.
     * Placeholders are linked with Campaigns which has all promotion attributes, UI components, messages, time frame etc.
     * These are all managed via the Crobox Admin application.
     *
     *  @param placeholderId Identifier of the placeholder
     *  @param queryParams Common query parameters, shared by the requests sent from the same page view
     *  @param impressions List of product ID's which promotions are requested for. It may be empty for the pages where products are not involved. (e.g. Checkout page)
     *  @param promotionCallback The callback to be notified for the response or if an error occurs before, during or after the request is sent
     *
     */
    fun promotions(
        placeholderId: Int,
        queryParams: RequestQueryParams,
        impressions: List<String> = emptyList(),
        promotionCallback: PromotionCallback
    ) {
        presenter.promotions(
            placeholderId = placeholderId,
            queryParams = queryParams,
            impressions = impressions,
            promotionCallback = promotionCallback
        )
    }

    /**
     * For sending a Click Event, to track the ratio of visits on impressions.
     *
     * Click events forms the measurement data for Click-through rate (CTR) for campaigns.
     *
     *  @param queryParams Common query parameters, shared by the requests sent from the same page view
     *  @param clickQueryParams Event specific query parameters for Click Events
     *  @param eventCallback The callback to be notified for the response or if an error occurs before, during or after the request is sent
     *
     */
    fun clickEvent(
        queryParams: RequestQueryParams,
        clickQueryParams: ClickQueryParams? = null,
        eventCallback: EventCallback
    ) {
        presenter.event(
            eventType = EventType.Click,
            queryParams = queryParams,
            additionalParams = clickQueryParams,
            eventCallback = eventCallback,
        )
    }


    /**
     * For sending an Add To Cart Event, to track the metrics of customer's intention of making a purchase.
     *
     *  @param queryParams Common query parameters, shared by the requests sent from the same page view
     *  @param cartQueryParams Event specific query parameters for AddToCart and RemoveFromCart Events
     *  @param eventCallback The callback to be notified for the response or if an error occurs before, during or after the request is sent
     *
     */
    fun addToCartEvent(
        queryParams: RequestQueryParams,
        cartQueryParams: CartQueryParams? = null,
        eventCallback: EventCallback
    ) {
        presenter.event(
            eventCallback = eventCallback,
            eventType = EventType.AddCart,
            queryParams = queryParams,
            additionalParams = cartQueryParams
        )
    }

    /**
     * For sending an Remove From Cart Event, to track the metrics of product's removal from a purchase.
     *
     *  @param queryParams Common query parameters, shared by the requests sent from the same page view
     *  @param cartQueryParams Event specific query parameters for AddToCart and RemoveFromCart Events
     *  @param eventCallback The callback to be notified for the response or if an error occurs before, during or after the request is sent
     *
     */
    fun removeFromCartEvent(
        queryParams: RequestQueryParams,
        cartQueryParams: CartQueryParams? = null,
        eventCallback: EventCallback
    ) {
        presenter.event(
            eventCallback = eventCallback,
            eventType = EventType.RemoveCart,
            queryParams = queryParams,
            additionalParams = cartQueryParams
        )
    }

    /**
     * For reporting a general-purpose error event
     *
     *  @param queryParams Common query parameters, shared by the requests sent from the same page view
     *  @param errorQueryParams Event specific query parameters for Error Events
     *  @param eventCallback The callback to be notified for the response or if an error occurs before, during or after the request is sent
     *
     */
    fun errorEvent(
        queryParams: RequestQueryParams,
        errorQueryParams: ErrorQueryParams? = null,
        eventCallback: EventCallback
    ) {
        presenter.event(
            eventCallback = eventCallback,
            eventType = EventType.Error,
            queryParams = queryParams,
            additionalParams = errorQueryParams
        )
    }


    /**
     * For reporting page view events
     *
     *  @param queryParams Common query parameters, shared by the requests sent from the same page view
     *  @param pageViewParams Event specific query parameters for Page View Events
     *  @param eventCallback The callback to be notified for the response or if an error occurs before, during or after the request is sent
     *
     */
    fun pageViewEvent(
        queryParams: RequestQueryParams,
        pageViewParams: PageViewParams? = null,
        eventCallback: EventCallback
    ) {
        presenter.event(
            eventCallback = eventCallback,
            eventType = EventType.PageView,
            queryParams = queryParams,
            additionalParams = pageViewParams
        )
    }

   /**
     * For reporting custom events
     *
     *  @param queryParams Common query parameters, shared by the requests sent from the same page view
     *  @param customQueryParams Event specific query parameters for Custom Events
     *  @param eventCallback The callback to be notified for the response or if an error occurs before, during or after the request is sent
     *
     */
    fun customEvent(
        queryParams: RequestQueryParams,
        customQueryParams: CustomQueryParams? = null,
        eventCallback: EventCallback
    ) {
        presenter.event(
            eventCallback = eventCallback,
            eventType = EventType.CustomEvent,
            queryParams = queryParams,
            additionalParams = customQueryParams
        )
    }


}

