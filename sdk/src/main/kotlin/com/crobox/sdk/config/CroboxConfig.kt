package com.crobox.sdk.config

import com.crobox.sdk.common.LocaleCode

data class CroboxConfig(
    val containerId: String,            // ContainerId (mandatory)
    val visitorId: String,              // VisitorId (mandatory)
    val currencyCode: String? = null,   // CurrencyCode (optional)
    val localeCode: LocaleCode? = null, // LocaleCode (optional)
    val userId: String? = null,         // UserId (optional)
    val timezone: Int? = null,          // Timezone (optional)
    val customProperties: Map<String, String>? = null, // Custom Properties (optional)
)
