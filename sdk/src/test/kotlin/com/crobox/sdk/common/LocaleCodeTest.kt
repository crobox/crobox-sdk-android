package com.crobox.sdk.common

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class LocaleCodeTest {

    @Test
    fun fromLocale() {
        assertEquals("en_US", LocaleCode.of(Locale.US).code)
        assertEquals("nl_BE", LocaleCode.of(Locale("nl", "BE")).code)
        assertEquals("fr", LocaleCode.of(Locale.FRENCH).code)
    }

    @Test
    fun legacyLanguageCodesAreReplaced() {
        assertEquals("he_IL", LocaleCode.of(Locale("iw", "IL")).code)
        assertEquals("id_ID", LocaleCode.of(Locale("in", "ID")).code)
    }

    @Test
    fun comparesByCode() {
        assertEquals(LocaleCode("en_US"), LocaleCode.of(Locale.US))
    }
}
