package com.crobox.sdk.core

import com.crobox.sdk.common.CroboxDebug
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.data.model.*
import com.crobox.sdk.presenter.CroboxAPIPresenter
import com.crobox.sdk.presenter.EventCallback
import com.crobox.sdk.presenter.PromotionCallback
import retrofit2.Call
import retrofit2.Response

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

    fun promotions(
        promotionCallback: PromotionCallback,
        placeholderId: String,
        queryParams: RequestQueryParams,
    ) {
        presenter.promotions(
            promotionCallback = promotionCallback,
            placeholderId = placeholderId,
            queryParams = queryParams
        )
    }

    /**
     * Invoked for a sending Click Events, to track the ratio of visits on impressions.
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

    fun addToCartEvent(
        queryParams: RequestQueryParams,
        addCartQueryParams: AddCartQueryParams? = null,
        eventCallback: EventCallback
    ) {
        presenter.event(
            eventCallback = eventCallback,
            eventType = EventType.AddCart,
            queryParams = queryParams,
            additionalParams = addCartQueryParams
        )
    }

    fun removeFromCartEvent(
        queryParams: RequestQueryParams,
        removeFromCartQueryParams: RemoveFromCartQueryParams? = null,
        eventCallback: EventCallback
    ) {
        presenter.event(
            eventCallback = eventCallback,
            eventType = EventType.RemoveCart,
            queryParams = queryParams,
            additionalParams = removeFromCartQueryParams
        )
    }

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

