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
     */
    fun promotions(
        placeholderId: Int,
        queryParams: RequestQueryParams,
        impressions: List<String>,
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
     */
    fun addToCartEvent(
        queryParams: RequestQueryParams,
        addCartQueryParams: CartQueryParams? = null,
        eventCallback: EventCallback
    ) {
        presenter.event(
            eventCallback = eventCallback,
            eventType = EventType.AddCart,
            queryParams = queryParams,
            additionalParams = addCartQueryParams
        )
    }

    /**
     * For sending an Remove From Cart Event, to track the metrics of product's removal from a purchase.
     *
     */
    fun removeFromCartEvent(
        queryParams: RequestQueryParams,
        removeFromCartQueryParams: CartQueryParams? = null,
        eventCallback: EventCallback
    ) {
        presenter.event(
            eventCallback = eventCallback,
            eventType = EventType.RemoveCart,
            queryParams = queryParams,
            additionalParams = removeFromCartQueryParams
        )
    }

    /**
     * For reporting a general-purpose error event
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

    fun pageViewEvent(
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

