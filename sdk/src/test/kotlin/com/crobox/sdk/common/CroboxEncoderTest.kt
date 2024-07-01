package com.crobox.sdk.common

import org.junit.Assert.*

import org.junit.Test
import java.util.UUID

class CroboxEncoderTest {

    @Test
    fun toBase36() {
        assertEquals("lu9znf91", CroboxEncoder.toBase36(1711554991093))
    }

}