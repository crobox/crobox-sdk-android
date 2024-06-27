package com.crobox.sdk.core

import com.crobox.sdk.common.CroboxDebug
import com.crobox.sdk.data.model.*
import com.crobox.sdk.presenter.CroboxAPIPresenter
import com.crobox.sdk.presenter.PromotionView
import com.crobox.sdk.presenter.EventView

class Crobox private constructor(config: CroboxConfig) {

    private val presenter = CroboxAPIPresenter(config)

    companion object {

        @Volatile
        private var instance: Crobox? = null

        fun getInstance(config: CroboxConfig): Crobox =
            instance ?: synchronized (this) {
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
        promotionView: PromotionView,
        placeholderId: String,
        queryParams: RequestQueryParams,
    ) {
        presenter.promotions(
            promotionView = promotionView,
            placeholderId = placeholderId,
            queryParams = queryParams
        )
    }

    // Usage functions
    fun clickEvent(
        queryParams: RequestQueryParams,
        clickQueryParams: ClickQueryParams? = null,
        eventView: EventView
    ) {
        presenter.event(
            eventView = eventView,
            eventType = EventType.Click,
            queryParams = queryParams,
            additionalParams = clickQueryParams
        )
    }

    fun addToCartEvent(
        queryParams: RequestQueryParams,
        addCartQueryParams: AddCartQueryParams? = null,
        eventView: EventView
    ) {
        presenter.event(
            eventView = eventView,
            eventType = EventType.AddCart,
            queryParams = queryParams,
            additionalParams = addCartQueryParams
        )
    }

    fun removeFromCartEvent(
        queryParams: RequestQueryParams,
        removeFromCartQueryParams: RemoveFromCartQueryParams? = null,
        eventView: EventView
    ) {
        presenter.event(
            eventView = eventView,
            eventType = EventType.RemoveCart,
            queryParams = queryParams,
            additionalParams = removeFromCartQueryParams
        )
    }

    fun errorEvent(
        queryParams: RequestQueryParams,
        errorQueryParams: ErrorQueryParams? = null,
        eventView: EventView
    ) {
        presenter.event(
            eventView = eventView,
            eventType = EventType.Error,
            queryParams = queryParams,
            additionalParams = errorQueryParams
        )
    }

    fun pageViewEvent(
        queryParams: RequestQueryParams,
        customQueryParams: CustomQueryParams? = null,
        eventView: EventView
    ) {
        presenter.event(
            eventView = eventView,
            eventType = EventType.CustomEvent,
            queryParams = queryParams,
            additionalParams = customQueryParams
        )
    }


}

