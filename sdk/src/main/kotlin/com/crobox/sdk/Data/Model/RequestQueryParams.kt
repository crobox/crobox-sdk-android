package com.crobox.sdk.data.model
import com.crobox.sdk.common.LocaleCode
import java.util.*
/*

 The list of query parameters that are applicable for mobile clients are below. These are applicable for both socket and promotions endpoints. Further details of each parameter are at the bottom. :

 User-Agent header for web
 Container ID
 Type: String
 Description: This is the unique id of the client's Crobox Container. It is generated and assigned by Crobox. So, this can be an alphanumeric constant configuration item, set only once by the sdk user.
 Example: cid="abc123"

 VisitorId
 Type: UUID
 Description: This is a randomly generated id that identifies a visitor / user. It must be the same across the user session (or even longer when possible). It must be in either plain UUIDv4 or Base64 string format. For simplicity, just pick Base64 for saving bandwidth space.
 UUID Example: vid=d4055206-fa5b-4eef-96c4-17e1c3a857fd
 Base64 Example: vid=1AVSBvpbTu-WxBfhw6hX_Q

 ViewId
 Type: UUID
 Description: This is a randomly generated id that identifies a unique page view. It is reused between various event and promotion requests while the user stays on the same page. Then, it is refreshed when a user goes to another page or reloads the same page. In other words, every socket or promotion request from the same view, should share the same ViewId. It must be in either plain UUID or Base64 string format. For simplicity, just pick Base64 for saving bandwidth space.
 UUID Example: vid=d4055206-fa5b-4eef-96c4-17e1c3a857fd
 Base64 Example: vid=1AVSBvpbTu-WxBfhw6hX_Q

 ViewCounter
 Type: Integer
 Description: Monotonically increasing counter, starting from 0. It is incremented per request in the same view. For instance, when the user is viewing and performing some actions on the same page, multiple promotion requests and events are sent. Among all these requests sharing the same ViewId, the counter should start from 0 and increment. This helps us group events by the view, and also guarantees for the uniqueness of a request/event when combined with ContainerId, VisitorId and ViewId.(Think of network failures or unintentional retries for example).
 Example: e=1

 OPTIONAL PARAMETERS

 LocaleCode
 Type: String
 Description: Locale code combination for the localization. It must be in {language}-{COUNTRY} format where

 the language must be lowercase, two-letter form of ISO 639-1 language codes
 the country must be uppercase, two-letter form of ISO 3166-1 Country codes
 Example: lc=en_US
 CurrencyCode
 Type: String
 Description: Contains information about the valid currency. It must be uppercase, three-letter form of ISO 4217 currency codes. It is useful when there are more than one currency configured in the Crobox Container.
 Example: cc=USD

 UserId
 Type: String
 Description: It is an identifier provided by the client that allows coupling of crobox user profiles with the client's user profiles, if available.
 Example: uid=abc123

 Timestamp
 Type: String
 Description: Timestamp as the millis since epoch, encoded with Base36.
 Example: ts=lu9znf91 (when timestamp is 1711554991093)

 Timezone
 Type: Integer
 Description: Current timezone of the user / device
 Example:tz=-4

 PageType
 Type: Integer
 Description: One of the values in predefined list of types of pages of the whole e-commerce funnel

 Custom Property [xyz]
 Type: String
 Description: Custom Property freely defined by the developer, using the prefix 'cp.'. This way, additional properties can be forwarded to Crobox endpoints, for example to help identifying certain traits of a visitor
 Example: cp.mobileUser=yes

 User-Agent header
 Description: This header is applicable for web clients but I wonder if it can be re-used for mobile clients as well, in order to distinguish between mobile devices / operating systems.
 */

// Mandatory
// "cid": ContainerId
// "e":  ViewCounter
// "vid": ViewId
// "pid": VisitorId

// Optional
// "cc":  CurrencyCode
// "lc" : LocaleCode
// "uid" : UserId
// "ts": Timestamp
// "tz": Timezone
// "pt" : PageType
// "cp.xyz" : Custom Property xyz

data class RequestQueryParams(
    val viewCounter: Int,               // ViewCounter (mandatory)
    val viewId: String,                 // ViewId (mandatory)
    val visitorId: String,              // VisitorId (mandatory)
    val currencyCode: String? = null,   // CurrencyCode (optional)
    val localeCode: LocaleCode? = null, // LocaleCode (optional)
    val userId: String? = null,         // UserId (optional)
    val timestamp: String? = null,      // Timestamp (optional)
    val timezone: Int? = null,          // Timezone (optional)
    var pageType: PageType? = null,     // PageType (optional)
    val customProperties: Map<String, String>? = null, // Custom Properties (optional)
    val pageUrl: String? = null,        // ViewController (optional)
    val referrerUrl: String? = null     // referrerUrl (optional)
)

/*

 PageType

 Type: Integer
 Description: One of the values in predefined list of types of pages of the whole e-commerce funnel

 PageOther = 0
 PageIndex = 1
 PageOverview = 2
 PageDetail = 3
 PageCart = 4
 PageCheckout = 5
 PageComplete = 6
 PageSearch = 7
 Example: pt=0
*/

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

/*

 EventType

 Type: String
 Description: Specifies the type of the event. It must be one of the following

*/

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

// "pi" : ProductId
// "cat" : Category
// "price" :  Price
// "qty": Quantity

data class ClickQueryParams(
    val productId: String? = null,
    val category: String? = null,
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
