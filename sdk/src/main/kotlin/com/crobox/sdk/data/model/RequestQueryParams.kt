package com.crobox.sdk.data.model

import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Common parameters for all requests sent from the same page view.
 *
 *  @param viewId : Unique identifier for a unique page viewing, reused between various event and promotion requests from the same page. It must be refreshed when a user goes to another page or reloads the same page.
 *  @param pageType : One of the values in predefined list of types of pages of the whole e-commerce funnel
 *  @param customProperties : Free format custom properties to be forwarded to Crobox endpoints, for example to help identifying certain traits of a visitor. Example: Map("mobileUser", "yes")
 *  @param pageName : Free format Page Name if exists
 */
data class RequestQueryParams(
    val viewId: UUID,                 // ViewId (mandatory)
    val pageType: PageType? = null,     // PageType (optional)
    val customProperties: Map<String, String>? = null, // Custom Properties (optional)
    val pageName: String? = null        // ViewController (optional)
) {
    private val counter = AtomicInteger(0)
    fun viewCounter(): Int = counter.getAndIncrement()
}

enum class PageType(val value: Int) {
    PageOther(0),
    PageIndex(1),
    PageOverview(2),
    PageDetail(3),
    PageCart(4),
    PageCheckout(5),
    PageComplete(6),
    PageSearch(7);
}

enum class EventType(val type: String) {
    Click("click"),           // Click
    AddCart("cart"),          // Add to Shopping Cart
    RemoveCart("rmcart"),     // Remove from Shopping Cart
    PageView("pageview"),     // PageView
    Error("error"),           // Error
    CustomEvent("event")      // CustomEvent
}

/**
 * Type specific parameters for Error events
 *
 *  @param tag : Free tagging for error categorisation
 *  @param name : Error name, if available
 *  @param message : Error message, if available
 *  @param file : File name, if available
 *  @param line : Line number, if available
 */

data class ErrorQueryParams(
    val tag: String? = null,
    val name: String? = null,
    val message: String? = null,
    val file: String? = null,
    val line: Int? = null,
)

/**
 * Type specific parameters for click events
 *
 *  @param productId : Unique identifier for a product
 *  @param price : Product price, if available
 *  @param quantity : Quantity, if available
 */
data class ClickQueryParams(
    val productId: String? = null,
    val price: Double? = null,
    val quantity: Int? = null
)

/**
 * Type specific parameters for Add/Remove Cart events
 *
 *  @param productId : Unique identifier for a product
 *  @param price : Product price, if available
 *  @param quantity : Quantity, if available
 */
data class CartQueryParams(
    val productId: String? = null,
    val price: Double? = null,
    val quantity: Int? = null
)

// "nm" : Name
// "promoId" : Promotion ID
// "pi" : Product ID
// "cat" : Category
// "price" : Price
// "qty" : Quantity

/**
 * Type specific parameters for general-purpose Custom events
 *
 *  @param name : Event name
 *  @param promotionId : Promotion Id, if available
 *  @param productId : Unique identifier for a product
 *  @param price : Product price, if available
 *  @param quantity : Quantity, if available
 */
data class CustomQueryParams(
    val name: String? = null,
    val promotionId: UUID? = null,
    val productId: String? = null,
    val price: Double? = null,
    val quantity: Int? = null
)
