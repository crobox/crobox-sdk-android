package com.crobox.sdk.config

import com.crobox.sdk.common.LocaleCode
import java.util.TimeZone
import java.util.UUID

/**
 * Configuration parameters for all requests
 *
 *  @param containerId This is the unique id of the Crobox Container as it is generated and assigned by Crobox.
 *  @param visitorId This is a randomly generated id that identifies a visitor / user. It must be the same across the user session (or even longer when possible).
 *  @param localeCode Locale of the visitor, for example `LocaleCode("en_US")`. It selects one of the regions configured for the container, and with it the currency; the container's fallback region is used when it is missing or matches none.
 *  @param userId It is an identifier that allows coupling between Crobox user profiles with the client's user profiles, if available.
 *  @param timezone Time zone of the visitor, to use instead of the device's, for example `TimeZone.getTimeZone("Europe/Amsterdam")`. It is sent as its offset from UTC in hours at the time of each request. When null, the device's time zone is used.
 *  @param customProperties Free format custom properties to be forwarded to Crobox endpoints with each request, for example to help identifying certain traits of a visitor. Example: Map("mobileUser", "yes")
 */
data class CroboxConfig(
    val containerId: String,
    val visitorId: UUID,
    val localeCode: LocaleCode? = null,
    val userId: String? = null,
    val timezone: TimeZone? = null,
    val customProperties: Map<String, String>? = null
)
