package com.crobox.sdk.config

import com.crobox.sdk.common.CurrencyCode
import com.crobox.sdk.common.LocaleCode
import java.util.UUID

/**
 * Configuration parameters for all requests
 *
 *  @param containerId This is the unique id of the Crobox Container as it is generated and assigned by Crobox.
 *  @param visitorId This is a randomly generated id that identifies a visitor / user. It must be the same across the user session (or even longer when possible).
 *  @param currencyCode Contains information about the valid currency. It must be uppercase, three-letter form of ISO 4217 currency codes. It is useful if there are more than one currency configured in the Crobox Container.
 *  @param localeCode Locale of the visitor, for example `LocaleCode("en_US")`. It selects one of the regions configured for the container; the container's fallback region is used when it is missing or matches none.
 *  @param userId It is an identifier that allows coupling between Crobox user profiles with the client's user profiles, if available.
 *  @param timezone Offset from UTC in whole hours, positive east of UTC (for example 1 for CET). When null, the device's current offset is sent.
 *  @param customProperties Free format custom properties to be forwarded to Crobox endpoints with each request, for example to help identifying certain traits of a visitor. Example: Map("mobileUser", "yes")
 */
data class CroboxConfig(
    val containerId: String,
    val visitorId: UUID,
    val currencyCode: CurrencyCode? = null,
    val localeCode: LocaleCode? = null,
    val userId: String? = null,
    val timezone: Int? = null,
    val customProperties: Map<String, String>? = null
)
