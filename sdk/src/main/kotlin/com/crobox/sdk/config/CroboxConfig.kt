package com.crobox.sdk.config

import com.crobox.sdk.common.LocaleCode

/**
 *
 *  Container ID
 *  Type: String
 *  Description: This is the unique id of the client's Crobox Container. It is generated and assigned by Crobox. So, this can be an alphanumeric constant configuration item, set only once by the sdk user.
 *  Example: cid="abc123"
 *
 *  VisitorId
 *  Type: UUID
 *  Description: This is a randomly generated id that identifies a visitor / user. It must be the same across the user session (or even longer when possible). It must be in either plain UUIDv4 or Base64 string format. For simplicity, just pick Base64 for saving bandwidth space.
 *  UUID Example: vid=d4055206-fa5b-4eef-96c4-17e1c3a857fd
 *  Base64 Example: vid=1AVSBvpbTu-WxBfhw6hX_Q
 *
 *
 *  UserId
 *  Type: String
 *  Description: It is an identifier provided by the client that allows coupling of crobox user profiles with the client's user profiles, if available.
 *  Example: uid=abc123
 *
 *
 *  LocaleCode
 *  Type: String
 *  Description: Locale code combination for the localization. It must be in {language}-{COUNTRY} format where
 *
 *  the language must be lowercase, two-letter form of ISO 639-1 language codes
 *  the country must be uppercase, two-letter form of ISO 3166-1 Country codes
 *  Example: lc=en_US
 *  CurrencyCode
 *  Type: String
 *  Description: Contains information about the valid currency. It must be uppercase, three-letter form of ISO 4217 currency codes. It is useful when there are more than one currency configured in the Crobox Container.
 *  Example: cc=USD
 *
 *
 */
data class CroboxConfig(
    val containerId: String,            // ContainerId (mandatory)
    val visitorId: String,              // VisitorId (mandatory)
    val currencyCode: String? = null,   // CurrencyCode (optional)
    val localeCode: LocaleCode? = null, // LocaleCode (optional)
    val userId: String? = null,         // UserId (optional)
    val timezone: Int? = null,          // Timezone (optional)
    val customProperties: Map<String, String>? = null, // Custom Properties (optional)
)
