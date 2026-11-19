package com.crobox.sdk.core

import com.crobox.sdk.common.CroboxDebug
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.data.model.CartParams
import com.crobox.sdk.data.model.CheckoutParams
import com.crobox.sdk.data.model.ClickParams
import com.crobox.sdk.data.model.CustomEventParams
import com.crobox.sdk.data.model.ErrorParams
import com.crobox.sdk.data.model.EventType
import com.crobox.sdk.data.model.PurchaseParams
import com.crobox.sdk.data.model.ViewContext
import com.crobox.sdk.data.model.ViewParams
import com.crobox.sdk.data.model.ViewType
import com.crobox.sdk.presenter.CroboxAPIPresenter
import com.crobox.sdk.presenter.PromotionCallback

class Crobox private constructor(config: CroboxConfig) {

    private val presenter = CroboxAPIPresenter(config)

    companion object {

        @Volatile
        private var instance: Crobox? = null

        /**
         * Returns the Crobox singleton, creating it with [config] on the first call.
         *
         * Later calls return the same instance and do not apply [config]. Use [updateConfig] to change the
         * configuration, for example to set the userId after login or to switch the locale (and with it the region and currency).
         */
        fun getInstance(config: CroboxConfig): Crobox =
            instance ?: synchronized(this) {
                instance ?: Crobox(config).also { instance = it }
            }
    }

    /**
     * The configuration used for every request.
     */
    val config: CroboxConfig
        get() = presenter.config

    /**
     * Replaces the configuration for all following requests.
     *
     * Use it to set or clear the userId on login or logout, or to switch the locale (and with it the region and currency):
     *
     * ```
     * crobox.updateConfig(crobox.config.copy(userId = "JohnDoe"))
     * ```
     *
     * Keep the same visitorId unless the visitor should become a new visitor, for example on logout.
     */
    fun updateConfig(config: CroboxConfig) {
        presenter.config = config
    }

    /**
     * Whether the SDK logs failed requests to Logcat, under the tag `croboxSDK`. Off by default.
     */
    var isLoggingEnabled: Boolean
        get() = CroboxDebug.loggingEnabled
        set(value) {
            CroboxDebug.loggingEnabled = value
        }


    /**
     * For retrieval of Promotions
     *
     * A Placement represents a predesignated point on the user interface, where the promotion will be located and displayed.
     * Placements are linked with Campaigns which has all promotion attributes, UI components, messages, time frame etc.
     * These are all managed via the Crobox Admin application.
     *
     *  @param placementId Identifier of the placement
     *  @param view Common parameters, shared by the requests sent from the same view
     *  @param productIds List of product ID's which promotions are requested for. It may be empty for the views where products are not involved. (e.g. Checkout view)
     *  @param callback The callback to be notified for the response or if an error occurs before, during or after the request is sent
     *
     */
    fun promotions(
        placementId: Int,
        view: ViewContext,
        productIds: List<String> = emptyList(),
        callback: PromotionCallback
    ) {
        presenter.promotions(
            placementId = placementId,
            view = view,
            productIds = productIds,
            callback = callback
        )
    }

    /**
     * For sending a Click Event, to track the ratio of visits on impressions.
     *
     * Click events forms the measurement data for Click-through rate (CTR) for campaigns.
     *
     *  @param view Common parameters, shared by the requests sent from the same view
     *  @param params Event specific parameters for Click Events
     */
    fun clickEvent(
        view: ViewContext,
        params: ClickParams? = null
    ) {
        presenter.event(
            eventType = EventType.Click,
            view = view,
            params = params
        )
    }


    /**
     * For sending an Add To Cart Event, to track the metrics of customer's intention of making a purchase.
     *
     *  @param view Common parameters, shared by the requests sent from the same view
     *  @param params Event specific parameters for AddToCart and RemoveFromCart Events
     */
    fun addToCartEvent(
        view: ViewContext,
        params: CartParams? = null
    ) {
        presenter.event(
            eventType = EventType.AddCart,
            view = view,
            params = params
        )
    }

    /**
     * For sending an Remove From Cart Event, to track the metrics of product's removal from a purchase.
     *
     *  @param view Common parameters, shared by the requests sent from the same view
     *  @param params Event specific parameters for AddToCart and RemoveFromCart Events
     */
    fun removeFromCartEvent(
        view: ViewContext,
        params: CartParams? = null
    ) {
        presenter.event(
            eventType = EventType.RemoveCart,
            view = view,
            params = params
        )
    }

    /**
     * For reporting a general-purpose error event
     *
     *  @param view Common parameters, shared by the requests sent from the same view
     *  @param params Event specific parameters for Error Events
     */
    fun errorEvent(
        view: ViewContext,
        params: ErrorParams? = null
    ) {
        presenter.event(
            eventType = EventType.Error,
            view = view,
            params = params
        )
    }


    /**
     * For reporting view events
     *
     *  @param view Common parameters, shared by the requests sent from the same view
     *  @param params Event specific parameters for View Events
     */
    fun viewEvent(view: ViewContext, params: ViewParams? = null) {
        presenter.event(
            eventType = EventType.PageView,
            view = view,
            params = params
        )
    }

    /**
     * For reporting checkout events
     *
     *  @param view Common parameters, shared by the requests sent from the same view
     *  @param params Event specific parameters for Checkout Events
     */
    fun checkoutEvent(view: ViewContext, params: CheckoutParams) {
        presenter.event(
            eventType = EventType.Checkout,
            view = view,
            params = params,
            viewType = ViewType.Checkout
        )
    }

    /**
     * For reporting purchase events
     *
     *  @param view Common parameters, shared by the requests sent from the same view
     *  @param params Event specific parameters for Purchase Events
     */
    fun purchaseEvent(view: ViewContext, params: PurchaseParams) {
        presenter.event(
            eventType = EventType.Purchase,
            view = view,
            params = params,
            viewType = ViewType.Complete
        )
    }

    /**
     * For reporting custom events
     *
     *  @param view Common parameters, shared by the requests sent from the same view
     *  @param params Event specific parameters for Custom Events
     */
    fun customEvent(
        view: ViewContext,
        params: CustomEventParams? = null,
    ) {
        presenter.event(
            eventType = EventType.CustomEvent,
            view = view,
            params = params
        )
    }

}
