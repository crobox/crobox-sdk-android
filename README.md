# crobox-sdk-android

Crobox SDK for Android

[![Build Status](https://github.com/crobox/crobox-sdk-android/actions/workflows/ci.yml/badge.svg)](https://github.com/crobox/crobox-sdk-android/actions?query=main)
[![Official Crobox project](https://img.shields.io/badge/project-official-green.svg?colorA=303033&colorB=ff8a2c&label=Crobox)](https://crobox.com/)
![GitHub Release](https://img.shields.io/github/v/release/crobox/crobox-sdk-android?include_prereleases)

This is an asynchronous SDK kit for consuming Crobox API for android applications. Written in Kotlin
from the ground up.

First add the dependency to your project:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.crobox.sdk:crobox-sdk-android:$crobox-sdk-android_version")
}
```

## Start using Crobox SDK

First configure and create a `Crobox` singleton as below, where

- `containerId` should be assigned by Crobox
- `visitorId` should be unique for visitors. It must stay the same across the user's session (or
  longer if preferred)
- `userId` should optionally be used to link the current visitor with client's user management
  system, if exists

```kotlin
import com.crobox.sdk.common.LocaleCode
import com.crobox.sdk.core.Crobox
import com.crobox.sdk.config.CroboxConfig
import com.crobox.sdk.data.model.*
import com.crobox.sdk.domain.*
import com.crobox.sdk.presenter.*
import java.util.UUID

val croboxInstance = Crobox.getInstance(
    CroboxConfig(
        containerId = "xlrc9t",
        visitorId = UUID.randomUUID(),
        userId = "JohnDoe"
    )
)

```

RequestQueryParams contains page specific parameters, shared by all requests fired from the same
page/view.
It must be recreated when the page/view is displayed.

```kotlin
val overviewPageParams = RequestQueryParams(
    viewId = UUID.randomUUID(),
    pageType = PageType.PageOverview
)
```

For sending events, use the `xyzEvent` APIs exposed by the Crobox instance.
Events might also take event specific parameters:

```kotlin
croboxInstance.clickEvent(
    overviewPageParams,
    clickQueryParams = ClickQueryParams(
        productId = "0001ABC",
        price = 1.0,
        quantity = 1
    ),
    eventCallback = object : EventCallback {
        override fun onSuccess(dictionary: Map<String, String>) {
            Log.d("EventView onSuccess", dictionary.toString())
        }

        override fun onError(msg: String?) {
            Log.d("EventView onError", "" + msg)
        }
    }
)
```

For retrieving promotions for zero, one or more products, use the specific PlaceholderId that is
configured with specific page types and linked to campaigns via Crobox Admin App.

```kotlin

val promotionsCallback = object : PromotionCallback {
    override fun onPromotions(response: PromotionsResponse?) {}

    override fun onError(msg: String?) {}
}

val impressions: List<String> = listOf("001ABC", "002DEF")

croboxInstance.promotions(
    placeholderId = 1,
    queryParams = overviewPageParams,
    impressions = impressions,
    promotionCallback = promotionsCallback
)

// Requesting for a promotion from a product detail page with another placeholderId for a single product
croboxInstance.promotions(
    placeholderId = 2,
    queryParams = detailPageParams,
    impressions = listOf("001ABC"),
    promotionCallback = promotionsCallback
)

// Requesting for a promotion without a product, eg. from a checkout page
croboxInstance.promotions(
    placeholderId = 3,
    queryParams = checkoutPageParams,
    promotionCallback = promotionsCallback
)
```

## Promotion Response Schema

```kotlin
object : PromotionCallback {
    override fun onPromotions(response: PromotionsResponse) {
        val context = response.context
        val promotions = response.promotions

        val visitorId = context.visitorId
        val sessionId = context.sessionId
        val groupName = context.groupName ?: ""
        for (campaign in context.campaigns) {
            val campaignId = campaign.id
            val campaignName = campaign.name
            val variantId = campaign.variantId
            val variantName = campaign.variantName
            val control = campaign.control
        }

        for (promotion in promotions) {
            val promotionId = promotion.id
            val campaignId = promotion.campaignId
            val variantId = promotion.variantId
            val productId = promotion.productId ?: ""
            promotion.content?.let { content: PromotionContent ->
                val messageId = content.messageId
                val componentName = content.component
                val configMap = content.config
                for (c in configMap) {
                    val configKey = c.key
                    val configValue = c.value
                }
                promotion.content?.contentConfig()?.let {
                  when (it) {
                    is SecondaryMessaging -> {
                      val name = it.name
                      val text = it.text
                      val fontColor = it.fontColor
                      val fontColorAndroid = it.fontColorAndroid() ?: Color.valueOf(Color.WHITE)
                    }
  
                    is TextBadge -> {
                      val name = it.name
                      val text = it.text
                      val fontColor = it.fontColor
                      val borderColor = it.borderColor
                      val backgroundColor = it.backgroundColor
                      val fontColorAndroid = it.fontColorAndroid() ?: Color.valueOf(Color.WHITE)
                      val borderColorAndroid = it.borderColorAndroid() ?: Color.valueOf(Color.WHITE)
                      val backgroundColorAndroid = it.backgroundColorAndroid() ?: Color.valueOf(Color.WHITE)
                    }
  
                    is ImageBadge -> {
                      val name = it.name
                      val image = it.image
                      val altText = it.altText
                    }
  
                  }
                }
            }
        }
    }
}
```

### PromotionsResponse

| Name       | Type             | Description                       |
|------------|------------------|-----------------------------------|
| context    | PromotionContext | The context about campaigns       |
| promotions | List<Promotion>  | The list of promotions calculated |

### PromotionContext

| Name      | Type           | Description                                      |
|-----------|----------------|--------------------------------------------------|
| sessionId | UUID           | Session ID                                       |
| visitorId | UUID           | Visitor ID                                       |
| groupName | String?        | The list of campaign and variant names, combined |
| campaigns | List<Campaign> | The list of ongoing campaigns                    |

### Campaign

| Name        | Type    | Description                                                                                                                                                                                                                                         |
|-------------|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| id          | String  | Campaign ID                                                                                                                                                                                                                                         |
| name        | String  | Campaign Name                                                                                                                                                                                                                                       |
| variantId   | String  | There is a ratio that determines the amount of traffic exposed to this campaign (or is allocated to the control group) between Crobox and Control group. Variant id refers to the variant which this promotion belongs to and is used for debugging |
| variantName | String  | Name of the Campaign Variant                                                                                                                                                                                                                        |  
| control     | Boolean | Indicates if the variant is allocated to the control group                                                                                                                                                                                          |

### Promotion

| Name       | Type              | Description                                          |
|------------|-------------------|------------------------------------------------------|
| id         | String            | Unique id for this promotion                         |
| productId  | String?           | Product ID if requested                              |
| campaignId | Int               | The campaign which this promotion belongs to         |
| variantId  | Int               | ID of the variant that this promotion is assigned to |
| content    | PromotionContent? | Promotion Content                                    |

### PromotionContent

| Name            | Type                    | Description                                                                                                                                                         |
|-----------------|-------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| messageId       | String                  | As Campaigns might have alternative messages, Message Id identifies the message assigned to this promotion                                                          |
| componentName   | String                  | Component Name                                                                                                                                                      |
| config          | Map<String, String>     | Map of all visual configuration items, managed via Crobox Admin app. <br/>Example:<br/> ```Map("Text1_text" : "Best Seller", "Text1_color" : "#0e1111")```          |
| contentConfig() | PromotionContentConfig? | Returns component configuration if an Image, Text Badge or Secondary Messaging is available as compliant. **Requires** prior configuration setup in Crobox platform |


### ImageBadge

| Name    | Type    | Description    |
|---------|---------|----------------|
| image   | String  | Image URL      |
| altText | String? | Alternate text |

### TextBadge

| Name            | Type    | Description               |
|-----------------|---------|---------------------------|
| text            | String  | Text message              |
| fontColor       | String  | Font color                |
| backgroundColor | String? | Optional background color |
| borderColor     | String? | Optional border color     |


### SecondaryMessaging

| Name            | Type    | Description               |
|-----------------|---------|---------------------------|
| text            | String  | Text message              |
| fontColor       | String  | Font color                |


## Samples

See [test app](app/src/main/kotlin/com/crobox/sdk/testapp/ui/DemoActivity.kt) to see details of Crobox SDK usage.

### List of screens
The application includes test UI for the next pages:  <br/>
**List of Products page**  - display list of test products with test promotion and banners<br/>
**Product details page**  - display product images, description and available variants.<br/>
**Product Basket page**  - display list of products in a user basket<br/>
**Checkout page**  - dialog with checkout data<br/>
**Purchase page** - final confirmation to complete purchase

### List of Events
Sample App sends Crobox event requests for the next scenarios:  <br/>
**clickEvent**  - used to track click events at object/ specific area.<br/>
```kotlin
(requireActivity() as DemoActivity).getCroboxAPI().clickEvent(product)
```
**pageViewEvent**  - used to track page view events, for example list of **products, product details, checkout, etc.<br/>
```kotlin
(requireActivity() as DemoActivity).getCroboxAPI().pageViewEvent("basket")
```
**addToCartEvent**  - used to track analytics when the user add products to the basket<br/>
```kotlin
(requireActivity() as DemoActivity).getCroboxAPI().addToCartEvent(purchaseItem)
```
**removeFromCartEvent**   - used to track analytics when the user removes/delete products to the basket<br/>
```kotlin
(requireActivity() as DemoActivity).getCroboxAPI().removeFromCartEvent(purchaseItem)
```
**checkOutEvent**  - used to track checkout page<br/>
```kotlin
(requireActivity() as DemoActivity).getCroboxAPI().checkOutEvent(BasketDataSource.items)
```
**purchaseEvent**  - used to track final confirmation for purchase<br/>
```kotlin
(requireActivity() as DemoActivity).getCroboxAPI().purchaseEvent(BasketDataSource.items)
```

### Get Promotion
The app executes **getPromotions** request to get a list of test promotions. These test promotions can be shown   
as a banner to notify end user about "Sales", and "Ads" about specific products.

### Sample data
Sample project uses test products and test data sources to visualize the general flow of E-Commerce application.   
That's why the application operates with a simplified set of data models, like:<br/>   
**Product** data model - specific Object that user can purchase  <br/>
**Variant** data model - Product with specific parameters (size, color, etc).<br/>
**PurchaseItem** data model - elements that the user puts into Basket.