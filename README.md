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
  override fun onPromotions(response: PromotionsResponse?) {
    // Promotion Context
    val context = response?.context
    val visitorId = response?.context?.visitorId
    val sessionId = response?.context?.sessionId
    val groupName = response?.context?.groupName
    response?.context?.campaigns?.let { campaigns: List<Campaign> ->
      for (campaign in campaigns) {
        val campaignId = campaign.id
        val campaignName = campaign.name
        val variantId = campaign.variantId
        val variantName = campaign.variantName
        val control = campaign.control
      }
    }
    // Promotions Calculated
    response?.promotions?.let { promotions: List<Promotion> ->
      for (promotion in promotions) {
        val promotionId = promotion.id
        val campaignId = promotion.campaignId
        val variantId = promotion.variantId
        promotion.content?.let { content: PromotionContent ->
          val messageId = content.id
          val componentName = content.component
          val config = content.config
        }
      }
    }

  }

  override fun onError(msg: String?) {

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
| groupName | String         | The list of campaign and variant names, combined |
| campaigns | List<Campaign> | The list of ongoing campaigns                    |

### Campaign

| Name        | Type    | Description                                                |
|-------------|---------|------------------------------------------------------------|
| id          | String  | Campaign ID                                                |
| name        | String  | Campaign Name                                              |
| variantId   | String  | Id of the Campaign Variant                                 |
| variantName | String  | Name of the Campaign Variant                               |  
| control     | Boolean | Indicates if the variant is allocated to the control group |

### Promotion

| Name       | Type             | Description                                      |
|------------|------------------|--------------------------------------------------|
| id         | String           | Unique id for this promotion                     |
| productId  | String           | Product ID that this promotion was requested for |
| campaignId | Int              | The campaign which this promotion belongs to     |
| variantId  | Int              | The variant which this promotion belongs to      |
| content    | PromotionContent | Promotion Content                                |

### PromotionContent

| Name      | Type                | Description                                                                                                                                                |
|-----------|---------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| id        | String              | Message Id of this promotion                                                                                                                               |
| component | String              | Component Name                                                                                                                                             |
| config    | Map<String, String> | Map of all visual configuration items, managed via Crobox Admin app. <br/>Example:<br/> ```Map("Text1_text" : "Best Seller", "Text1_color" : "#0e1111")``` |

## Samples

See [test app](app/src/main/kotlin/com/crobox/sdk/testapp/MainActivity.kt) for various samples

