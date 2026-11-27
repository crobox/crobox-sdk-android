# crobox-sdk-android

Crobox SDK for Android

[![Build Status](https://github.com/crobox/crobox-sdk-android/actions/workflows/ci.yml/badge.svg)](https://github.com/crobox/crobox-sdk-android/actions?query=main)
[![Official Crobox project](https://img.shields.io/badge/project-official-green.svg?colorA=303033&colorB=ff8a2c&label=Crobox)](https://crobox.com/)
![GitHub Release](https://img.shields.io/github/v/release/crobox/crobox-sdk-android?include_prereleases)

This is an asynchronous SDK kit for consuming Crobox API for android applications. Written in Kotlin
from the ground up.

The SDK is published to Maven Central and needs `minSdk` 24 (Android 7.0) or later. Add the
dependency to your project:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.crobox.sdk:crobox-sdk-android:2.0.1")
}
```

See the [releases](https://github.com/crobox/crobox-sdk-android/releases) for the latest version.

### Upgrading to 2.0

Version 2.0 names the API after views in an app, the way the iOS SDK does, and gives the App components
ready-to-use colors and backgrounds. What to change:

| 1.x                                                                  | 2.0                                                                  |
|----------------------------------------------------------------------|----------------------------------------------------------------------|
| `enableLogging()`, `disableLogging()`                                | `isLoggingEnabled = true` / `false`                                  |
| `RequestQueryParams(viewId, pageType, customProperties, pageName)`   | `ViewContext(viewId, viewType, customProperties, viewName)`          |
| `PageType.PageDetail`, `PageType.PageOverview`, ...                  | `ViewType.Detail`, `ViewType.Overview`, ...                          |
| `clickEvent(queryParams = ..., clickQueryParams = ...)`, and the other events | `clickEvent(view = ..., params = ...)`                      |
| `pageViewEvent(...)` with `PageViewParams(pageTitle = ...)`          | `viewEvent(...)` with `ViewParams(title = ...)`                      |
| `ClickQueryParams`, `CartQueryParams`, `ErrorQueryParams`, `CustomQueryParams` | `ClickParams`, `CartParams`, `ErrorParams`, `CustomEventParams` |
| `promotions(placeholderId, queryParams, impressions, promotionCallback)` | `promotions(placementId, view, productIds, callback)`            |
| `PromotionCallback.onError(msg: String)`                             | `onError(error: CroboxError)`: `HttpError`, `RequestFailed` or `InvalidResponse` |
| `Campaign.id`, `Campaign.variantId` as `String`                      | `Int`                                                                |
| `PromotionContent.contentConfig()`                                   | `component()`, a sealed `PromotionComponent`                         |
| `TextBadge.fontColor` / `borderColor` (CSS strings) and `fontColorAndroid()` / `borderColorAndroid()` | `fontColor` / `borderColor` as color ints |
| `TextBadge.backgroundColor` and `backgroundColorAndroid()`           | `backgroundDrawable(cornerRadius)` or `backgroundShader(width, height)`, for a color or a gradient |
| `SecondaryMessaging.fontColor` and `fontColorAndroid()`              | `fontColor` as a color int                                           |
| `ImageBadge.image`                                                   | `imageUrl`                                                           |
| `ImageBadge.name`, `TextBadge.name`, `SecondaryMessaging.name`       | `PromotionContent.componentName`                                     |

The raw settings of a promotion are still in `PromotionContent.config`. What the SDK sends to Crobox is unchanged.

## Start using Crobox SDK

First configure and create a `Crobox` singleton as below, where

- `containerId` should be assigned by Crobox
- `visitorId` identifies the visitor. It must stay the same across app launches: Crobox assigns
  campaign variants and the control group per visitor, so a new visitorId on every launch re-buckets
  the user and makes A/B results unreliable. Use `CroboxVisitor.visitorId(context)`, which stores it
  on the device, or persist your own. Pass `null` while the user hasn't consented to tracking; see
  [Consent](#consent)
- `localeCode` selects one of the regions (language, country and currency) configured for your
  container in Crobox, for example `LocaleCode("en_US")` or `LocaleCode.of(Locale.getDefault())`.
  Without it, or when it matches no region, the container's fallback region is used
- `userId` should optionally be used to link the current visitor with client's user management
  system, if exists
- `timezone` is optional. The device's time zone is sent by default; set it, for example to
  `TimeZone.getTimeZone("Europe/Amsterdam")`, only to use a different one

```kotlin
import com.crobox.sdk.common.CroboxVisitor
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
        visitorId = CroboxVisitor.visitorId(context),
        localeCode = LocaleCode("en_US"),
        userId = "JohnDoe"
    )
)

```

`getInstance` applies the config only on the first call. To change it later, for example to set the
`userId` after login or to switch the locale (and with it the region and currency), use `updateConfig`:

```kotlin
croboxInstance.updateConfig(croboxInstance.config.copy(userId = "JohnDoe"))

// On logout, when the next person on the device should be a new visitor
croboxInstance.updateConfig(
    croboxInstance.config.copy(userId = null, visitorId = CroboxVisitor.reset(context))
)
```

### Consent

The `visitorId` is what makes the SDK track the user. Without one, the SDK works the way the website does
for a visitor who opted out:

- It sends no events at all, error events included.
- It still requests promotions, so the app can show campaign messages, but without the `visitorId`,
  view id and `userId`, and marked "do not persist": Crobox stores nothing about the request. Crobox
  picks the campaign variant from an anonymous id that changes every day, and `context.visitorId` in
  the response is that id.

If your app needs consent before tracking, pass `visitorId = null` until the user gives it:

```kotlin
val crobox = Crobox.getInstance(
    CroboxConfig(
        containerId = "xlrc9t",
        visitorId = if (hasConsent) CroboxVisitor.visitorId(context) else null
    )
)

// The user consents
crobox.updateConfig(crobox.config.copy(visitorId = CroboxVisitor.visitorId(context)))

// The user withdraws consent: stop tracking, and start as a new visitor if they consent again
CroboxVisitor.reset(context)
crobox.updateConfig(crobox.config.copy(visitorId = null))
```

A `ViewContext` contains view specific parameters, shared by all requests fired from the same
view. Create **one per screen the user sees**, and use it for the view event and for every
promotions and event request on that screen. Create a new one when the user goes to another screen
or comes back to this one. The server counts every new `viewId` as a page, so a new one per request
double-counts pages, and one for the whole app merges every screen into a single page.

```kotlin
val overviewView = ViewContext(
    viewId = UUID.randomUUID(),
    viewType = ViewType.Overview
)
```

For sending events, use the `xyzEvent` APIs exposed by the Crobox instance.
Events might also take event specific parameters:

```kotlin
croboxInstance.clickEvent(
    overviewView,
    params = ClickParams(
        productId = "0001ABC",
        quantity = 1
    )
)

// The products shown in the view, in the order they are shown, optionally with the list they are in
croboxInstance.viewEvent(
    overviewView,
    ViewParams(
        impressions = listOf(
            ProductParams(productId = "0001ABC", list = "search results"),
            ProductParams(productId = "0002DEF", list = "search results")
        )
    )
)
```

A purchase is only recorded with a `transactionId`, so `PurchaseParams` requires one.

For retrieving promotions for zero, one or more products, use the specific `placementId` that is
configured with specific view types and linked to campaigns via Crobox Admin App.

```kotlin

val promotionsCallback = object : PromotionCallback {
    override fun onPromotions(response: PromotionsResponse) {}

    override fun onError(error: CroboxError) {
        when (error) {
            is CroboxError.HttpError -> {}        // error.statusCode, for example 404 for an unknown container
            is CroboxError.RequestFailed -> {}    // no answer, for example no network or a timeout
            is CroboxError.InvalidResponse -> {}  // an answer that could not be read
        }
    }
}

val productIds: List<String> = listOf("001ABC", "002DEF")

croboxInstance.promotions(
    placementId = 1,
    view = overviewView,
    productIds = productIds,
    callback = promotionsCallback
)

// Requesting for a promotion from a product detail view with another placementId for a single product
croboxInstance.promotions(
    placementId = 2,
    view = detailView,
    productIds = listOf("001ABC"),
    callback = promotionsCallback
)

// Requesting for a promotion without a product, eg. from a checkout view
croboxInstance.promotions(
    placementId = 3,
    view = checkoutView,
    callback = promotionsCallback
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
            val productId = promotion.productId ?: ""
            promotion.content?.let { content: PromotionContent ->
                val messageId = content.messageId
                val componentName = content.componentName
                val configMap = content.config
                for (c in configMap) {
                    val configKey = c.key
                    val configValue = c.value
                }
                when (val component = content.component()) {
                    is TextBadge -> {
                        badgeView.text = component.text
                        component.fontColor?.let { badgeView.setTextColor(it) }
                        // The background, a plain color or a gradient
                        badgeView.background = component.backgroundDrawable(cornerRadius = 8f)
                    }

                    is ImageBadge -> {
                        val imageUrl = component.imageUrl
                        val altText = component.altText
                    }

                    is SecondaryMessaging -> {
                        messageView.text = component.text
                        component.fontColor?.let { messageView.setTextColor(it) }
                    }

                    null -> {}
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
| visitorId | UUID           | Visitor ID; without a configured `visitorId`, the anonymous id of the day |
| groupName | String?        | The list of campaign and variant names, combined |
| campaigns | List<Campaign> | The list of ongoing campaigns                    |

### Campaign

| Name        | Type    | Description                                                                                                                                                                                                                                         |
|-------------|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| id          | Int     | Campaign ID                                                                                                                                                                                                                                         |
| name        | String  | Campaign Name                                                                                                                                                                                                                                       |
| variantId   | Int     | There is a ratio that determines the amount of traffic exposed to this campaign (or is allocated to the control group) between Crobox and Control group. Variant id refers to the variant which this promotion belongs to and is used for debugging |
| variantName | String  | Name of the Campaign Variant                                                                                                                                                                                                                        |  
| control     | Boolean | Indicates if the variant is allocated to the control group                                                                                                                                                                                          |

### Promotion

| Name       | Type              | Description                                          |
|------------|-------------------|------------------------------------------------------|
| id         | UUID              | Unique id for this promotion                         |
| productId  | String?           | Product ID if requested                              |
| campaignId | Int               | The campaign which this promotion belongs to         |
| content    | PromotionContent? | Promotion Content                                    |

The variant a promotion belongs to is the `variantId` of the campaign in `context.campaigns` with the same `campaignId`.

### PromotionContent

| Name            | Type                    | Description                                                                                                                                                         |
|-----------------|-------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| messageId       | String                  | As Campaigns might have alternative messages, Message Id identifies the message assigned to this promotion                                                          |
| componentName   | String                  | Name of the campaign's component, as a module name, e.g. `MobAppTextBadgeTsx`                                                                                       |
| config          | Map<String, String>     | Map of all visual configuration items, managed via Crobox Admin app. <br/>Example:<br/> ```Map("Text1_text" : "Best Seller", "Text1_color" : "#0e1111")```          |
| component()     | PromotionComponent?     | What the promotion shows when the campaign uses an App component: a `TextBadge` (`MobAppTextBadgeTsx`), an `ImageBadge` (`MobAppImageBadgeTsx`) or `SecondaryMessaging` (`MobAppSecondaryMessagingTsx`), otherwise null |


### ImageBadge

| Name     | Type    | Description                                |
|----------|---------|--------------------------------------------|
| imageUrl | String  | The image's URL                            |
| altText  | String? | Text that describes the image, for screen readers |

### TextBadge

| Name                             | Type      | Description                                                                                                       |
|----------------------------------|-----------|-------------------------------------------------------------------------------------------------------------------|
| text                             | String    | The text                                                                                                          |
| fontColor                        | Int?      | The text's color as a color int, for `TextView.setTextColor`                                                     |
| borderColor                      | Int?      | The border's color as a color int                                                                                 |
| backgroundDrawable(cornerRadius) | Drawable? | The background, a plain color or a gradient, as a view's background, with rounded corners of `cornerRadius` pixels |
| backgroundShader(width, height)  | Shader?   | The background, a plain color or a gradient, for an area of that size, for example for a Compose `ShaderBrush`     |

The background is set in the App as a color, or as a gradient with its gradient input. Gradients look as they do in the
App's preview: a linear gradient's angle runs clockwise from the bottom up (90deg runs from left to right), and a radial
gradient runs from the center out to the farthest corner.

### SecondaryMessaging

| Name      | Type   | Description                                                    |
|-----------|--------|----------------------------------------------------------------|
| text      | String | The text                                                       |
| fontColor | Int?   | The text's color as a color int, for `TextView.setTextColor`   |

## Samples

See [test app](app/src/main/kotlin/com/crobox/sdk/testapp/ui/DemoActivity.kt) to see details of Crobox SDK usage.

### List of screens
The application includes test UI for the next pages:  <br/>
**List of Products page**  - display list of test products with test promotion and banners<br/>
**Product Details page**  - display product images, description and available variants.<br/>
**Product Basket page**  - display list of products in a user basket<br/>
**Checkout page**  - dialog with checkout data<br/>
**Purchase page** - final confirmation to complete purchase

### List of Events
Sample App sends Crobox event requests for the next scenarios:  <br/>
**clickEvent**  - used to track click events at object/ specific area.<br/>
```kotlin
(requireActivity() as DemoActivity).getCroboxAPI().clickEvent(product)
```
**viewEvent**  - used to track view events, for example list of **products, product details, checkout, etc.<br/>
```kotlin
(requireActivity() as DemoActivity).getCroboxAPI().viewEvent("basket")
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

## Development

### Build and test

Building needs JDK 17 or later, which Android Gradle Plugin 9 and Gradle 9 require. CI and the release
workflow use 21, the JDK Android Studio ships with.

```shell
./gradlew build                    # compile, lint and run all tests (what CI runs)
./gradlew :sdk:testDebugUnitTest   # SDK tests only
```

The tests in `sdk/src/test` include two integration tests that call the live Crobox API with the
test container `xlrc9t`, so they need network access:

- `PromotionsIT` requests promotions and checks that the response parses.
- `EventsIT` sends one of each event type. It asserts nothing; check that the events arrive in the
  Crobox dashboard for container `xlrc9t`.

They run as part of `./gradlew build` and therefore on every CI build.

The sample app in `app/` uses the SDK from source and is the quickest way to try a change on a device.

### Releasing

Releases are made by the [Release](.github/workflows/release.yml) workflow (Actions → Release → Run
workflow, or `gh workflow run release.yml -f version=X.Y.Z`). Enter the new version, for example
`1.1.0`. Follow semver: removing or changing public API needs a new major version.

The workflow:

1. Sets `VERSION_NAME` in [gradle.properties](gradle.properties). The SDK sends it in its `User-Agent`
   header.
2. Commits the version on top of `main` as "Release X.Y.Z" and builds it, running all tests.
3. Signs the artifacts and publishes them to Maven Central (`com.crobox.sdk:crobox-sdk-android`)
   through the [Central Portal](https://central.sonatype.com). It can take up to an hour before the
   version resolves from Maven Central.
4. Tags the release commit `vX.Y.Z` and pushes the tag.
5. Creates the GitHub release, with notes generated from the merged pull requests.

Branch protection does not let the workflow push to `main`, so the release commit exists only on its
tag, and `VERSION_NAME` on `main` stays at its `-SNAPSHOT` version. After a release, bump it on `main`
in a pull request to the next patch version, for example `1.1.1-SNAPSHOT` after `1.1.0`. If publishing
fails, nothing is tagged, so fix the cause and run the workflow again.

The workflow reads these repository secrets:

| Secret                     | Content                                                            |
|----------------------------|--------------------------------------------------------------------|
| `SONATYPE_NEXUS_TOKENNAME` | Username of a Central Portal user token (*Account → Generate User Token*) |
| `SONATYPE_NEXUS_TOKEN`     | Password of that user token                                        |
| `SIGNING_PRIVATE_KEY2`     | ASCII-armored GPG private key used to sign the artifacts           |
| `SIGNING_PASSWORD2`        | Passphrase of that key                                             |

The token must belong to a Central Portal account with access to the `com.crobox.sdk` namespace.
