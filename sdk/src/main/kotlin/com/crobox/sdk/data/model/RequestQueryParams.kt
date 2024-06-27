package com.crobox.sdk.data.model

import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Common parameters for all requests
 *
 *  @param viewId : Unique identifier for a unique page viewing, reused between various event and promotion requests. It must be refreshed when a user goes to another page or reloads the same page.
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

/*

 The following arguments are applicable for error events (where t=error). They are all optional.

 Tag
 Type: String
 Description: Free text for categorization of errors by the developer
 Example: tg=promotionAction

 Name
 Type: String
 Description: Free text for naming of errors by the developer
 Example: nm=TypeError

 Message
 Type: String
 Description: Free text for description of errors by the developer
 Example: msg=Cannot read product properties

 File
 Type: String
 Description: Free text for a file name, if specified
 Example: f=SomeFile.txt

 Line
 Type: Int
 Description: Simple numeric value to indicate the line number
 Example: l=2

 Device Pixel Ratio
 Type: Double
 Description: From https://stackoverflow.com/a/8785677

 The device pixel ratio is the ratio between physical pixels and logical pixels. For instance, the iPhone 4 and iPhone 4S report a device pixel ratio of 2, because the physical linear resolution is double the logical linear resolution.
 Physical resolution: 960 x 640
 Logical resolution: 480 x 320
 Example: dpr=2

 Device Language
 Type: String
 Description: Device language in the form of [language]-[country], with 2 lowercase letters each. Technically, it can be different than the lc locale, for instance lc=en-CA&ul=en-us can be a valid combination of a device from Canada
 Example: ul=en-us

 View Port size
 Type:String
 Description: From https://stackoverflow.com/a/73927240

 Your screen size is the number of pixels in the total width and height of your computer. The size of your viewport is the number of pixels in the browser window on your screen, so it is quite normal for the viewport to be smaller than the screen size.
 Example: vp=1173x686

 Screen Resolution Size
 Type: String
 Description: The whole screen size of the device
 Example: sr=1440x900
*/

// "tg" : Tag
// "nm" : Name
// "msg" : Message
// "f" : File
// "l" : Line
// "dpr" : Device Pixel Ratio
// "ul" : Device Language
// "vp" : View Port size
// "sr" : Screen Resolution Size

data class ErrorQueryParams(
    val tag: String? = null,
    val name: String? = null,
    val message: String? = null,
    val file: String? = null,
    val line: Int? = null,
    val devicePixelRatio: Double? = null,
    val deviceLanguage: String? = null,
    val viewPortSize: String? = null,
    val screenResolutionSize: String? = null
)

/*

 The following arguments are applicable for AddToCart events (where t=cart). They are all optional

 ProductId
 Type: String
 Description: Identifier of a product.
 Example: pi=abcd-123

 Category
 Type: String
 Description: Category of a product.
 Example: cat=mens_shoes

 Price
 Type: Double
 Description: Numeric digits separated by comma or dot
 Example: price=123,456 or 123.456

 Quantity
 Type: Int
 Description: Numeric integer indicating the number of products
 Example: qty=2

*/

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

data class AddCartQueryParams(
    val productId: String? = null,
    val category: String? = null,
    val price: Double? = null,
    val quantity: Int? = null
)

data class RemoveFromCartQueryParams(
    val productId: String? = null,
    val category: String? = null,
    val price: Double? = null,
    val quantity: Int? = null
)

/*

 The following arguments are applicable for click events (where t=event). They are all optional

 Name
 Type: String
 Description: Custom event name
 Example: nm=banner-click

 Promotion ID
 Type: UUID
 Description: Promotion identifier
 Example: promoId=c97c02c0-1f5a-11ef-81c0-cd67b451d09e

 Product ID
 Type: String
 Description: Identifier of a product.
 Example: pi=abcd-123

 Category
 Type: String
 Description: Category of a product.
 Example: cat=mens_shoes

 Price
 Type: Double
 Description: Numeric digits separated by comma or dot
 Example: price=123,456 or 123.456

 Quantity
 Type: Int
 Description: Numeric integer indicating the number of products
 Example: qty=2

*/

// "nm" : Name
// "promoId" : Promotion ID
// "pi" : Product ID
// "cat" : Category
// "price" : Price
// "qty" : Quantity

data class CustomQueryParams(
    val name: String? = null,
    val promotionId: UUID? = null,
    val productId: String? = null,
    val category: String? = null,
    val price: Double? = null,
    val quantity: Int? = null
)
