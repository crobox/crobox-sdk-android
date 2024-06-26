package com.crobox.sdk.core

import com.crobox.sdk.common.CroboxDebug
import com.crobox.sdk.data.model.*
import com.crobox.sdk.presenter.CroboxAPIPresenter
import com.crobox.sdk.presenter.PromotionView
import com.crobox.sdk.presenter.SocketView

class Crobox private constructor(containerId: String) {

    private val presenter = CroboxAPIPresenter(containerId)

    companion object {

        @Volatile
        private var instance: Crobox? = null

        fun getInstance(containerId: String): Crobox =
            instance ?: synchronized (this) {
                instance ?: Crobox(containerId).also { instance = it }
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
    fun pageViewClick(
        queryParams: RequestQueryParams,
        clickQueryParams: ClickQueryParams? = null,
        socketView: SocketView
    ) {
        presenter.socket(
            socketView = socketView,
            eventType = EventType.Click,
            queryParams = queryParams,
            additionalParams = clickQueryParams
        )
    }

    fun pageViewAddCart(
        queryParams: RequestQueryParams,
        addCartQueryParams: AddCartQueryParams? = null,
        socketView: SocketView
    ) {
        presenter.socket(
            socketView = socketView,
            eventType = EventType.AddCart,
            queryParams = queryParams,
            additionalParams = addCartQueryParams
        )
    }

    fun pageViewRemoveCart(
        queryParams: RequestQueryParams,
        removeFromCartQueryParams: RemoveFromCartQueryParams? = null,
        socketView: SocketView
    ) {
        presenter.socket(
            socketView = socketView,
            eventType = EventType.RemoveCart,
            queryParams = queryParams,
            additionalParams = removeFromCartQueryParams
        )
    }

    fun pageViewError(
        queryParams: RequestQueryParams,
        errorQueryParams: ErrorQueryParams? = null,
        socketView: SocketView
    ) {
        presenter.socket(
            socketView = socketView,
            eventType = EventType.Error,
            queryParams = queryParams,
            additionalParams = errorQueryParams
        )
    }

    fun pageViewEvent(
        queryParams: RequestQueryParams,
        customQueryParams: CustomQueryParams? = null,
        socketView: SocketView
    ) {
        presenter.socket(
            socketView = socketView,
            eventType = EventType.CustomEvent,
            queryParams = queryParams,
            additionalParams = customQueryParams
        )
    }


}

