package com.crobox.sdk.common

import java.util.Locale

/**
 * The locale of the visitor, in the form `{language}_{COUNTRY}`, for example `en_US`, where
 *  - the language is the lowercase, two-letter ISO 639-1 language code
 *  - the country is the uppercase, two-letter ISO 3166-1 country code
 *
 * Crobox uses it to select one of the regions configured for your container, and with it the language, country and
 * currency of the request. Use a code that matches one of those regions. When none matches, the container's
 * fallback region is used.
 *
 * ```
 * LocaleCode("en_US")
 * LocaleCode.of(Locale.getDefault())
 * ```
 */
class LocaleCode(val code: String) {

    companion object {

        /**
         * Creates a LocaleCode from a [Locale], for example `en_US` from `Locale.US`, or `en` for a locale without a
         * country.
         */
        @JvmStatic
        fun of(locale: Locale): LocaleCode {
            // Older Android and Java versions report the legacy codes for Hebrew, Indonesian and Yiddish
            val language = when (val language = locale.language) {
                "iw" -> "he"
                "in" -> "id"
                "ji" -> "yi"
                else -> language
            }
            return LocaleCode(if (locale.country.isEmpty()) language else "${language}_${locale.country}")
        }
    }

    override fun equals(other: Any?): Boolean = other is LocaleCode && other.code == code

    override fun hashCode(): Int = code.hashCode()

    override fun toString(): String = code
}
