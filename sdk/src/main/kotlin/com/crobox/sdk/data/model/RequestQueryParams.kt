package com.crobox.sdk.data.model

import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

/**
 * Common parameters for all requests sent from the same page view.
 *
 *  @param viewId Unique identifier for a unique page viewing, reused between various event and promotion requests from the same page. It must be refreshed when a user goes to another page or reloads the same page.
 *  @param pageType One of the values in predefined list of types of pages of the whole e-commerce funnel
 *  @param customProperties Free format custom properties to be forwarded to Crobox endpoints, for example to help identifying certain traits of a visitor. Example: Map("mobileUser", "yes")
 *  @param pageName Free format Page Name if exists
 */
data class RequestQueryParams(
    val viewId: UUID,
    val pageType: PageType,
    val customProperties: Map<String, String>? = null,
    val pageName: String? = null
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

enum class EventType(internal val type: String) {
    Click("click"),           // Click
    AddCart("cart"),          // Add to Shopping Cart
    RemoveCart("rmcart"),     // Remove from Shopping Cart
    PageView("pageview"),     // PageView
    Checkout("pageview"),     // PageView
    Purchase("pageview"),     // PageView
    Error("error"),           // Error
    CustomEvent("event")      // CustomEvent
}

/**
 * Type specific parameters for Error events
 *
 *  @param tag Free tagging for error categorisation
 *  @param name Error name, if available
 *  @param message Error message, if available
 *  @param file File name, if available
 *  @param line Line number, if available
 */

data class ErrorQueryParams(
    val tag: String? = null,
    val name: String? = null,
    var message: String? = null,
    val file: String? = null,
    val line: Int? = null,
)

/**
 * Type specific parameters for click events
 *
 *  @param productId Unique identifier for a product
 *  @param price Product price, if available
 *  @param quantity Quantity, if available
 */
data class ClickQueryParams(
    val productId: String? = null,
    val price: Double? = null,
    val quantity: Int? = null
)

/**
 * Type specific parameters for Add/Remove Cart events
 *
 *  @param productId Unique identifier for a product
 *  @param price Product price, if available
 *  @param quantity Quantity, if available
 */
data class CartQueryParams(
    val productId: String? = null,
    val price: Double? = null,
    val quantity: Int? = null
)

/**
 * Type specific parameters for general-purpose Custom events
 *
 *  @param name Event name
 *  @param promotionId Promotion Id, if available
 *  @param productId Unique identifier for a product
 *  @param price Product price, if available
 *  @param quantity Quantity, if available
 */
data class CustomQueryParams(
    val name: String? = null,
    val promotionId: UUID? = null,
    val productId: String? = null,
    val price: Double? = null,
    val quantity: Int? = null
)

/**
 * Type specific parameters for Page View events
 *
 *  @param pageTitle Optional Page title
 *  @param product Optional product specific parameters
 *  @param searchTerms Optional search terms that led the viewer to this product
 *  @param impressions Optional list of products that are viewed in the same page
 *  @param customProperties Optional set of general purpose custom properties, for example to help identifying certain traits of the page
 */
data class PageViewParams(
    val pageTitle: String? = null,
    val product: ProductParams? = null,
    val searchTerms: String? = null,
    val impressions: Set<ProductParams>? = null,
    val customProperties: Map<String, String>? = emptyMap()
)

/**
 * Type specific parameters for Purchase events
 *
 *  @param products Optional set of products purchased
 *  @param transactionId Optional transaction identifier
 *  @param affiliation Optional store or affiliation from which this transaction occurred (e.g. Google Store)
 *  @param coupon Optional coupon
 *  @param revenue Optional the total associated with the transaction
 *  @param customProperties Optional set of general purpose custom properties, for example to help identifying certain traits of the page
 */
data class PurchaseParams(
    val products: Set<ProductParams>? = null,
    val transactionId: String? = null,
    val affiliation: String? = null,
    val coupon: String? = null,
    val revenue: Double? = null,
    val customProperties: Map<String, String>? = emptyMap()
)

/**
 * Type specific parameters for Checkout events
 *
 *  @param products Optional set of products to be purchased
 *  @param step  Optional number representing a step in the checkout process
 *  @param customProperties Optional set of general purpose custom properties, for example to help identifying certain traits of the page
 */
data class CheckoutParams(
    val products: Set<ProductParams>? = null,
    val step: String? = null,
    val customProperties: Map<String, String>? = emptyMap()
)

/**
 * Product specific parameters for various events
 *
 *  @param productId Product identifier
 *  @param price Optional Product price
 *  @param quantity  Optional quantity
 *  @param otherProductIds Optional set of accompanying productIds which this particular product belongs to
 */
data class ProductParams(
    val productId: String,
    val price: Double? = null,
    val quantity: Int? = null,
    val otherProductIds: Set<String>? = emptySet()
)
