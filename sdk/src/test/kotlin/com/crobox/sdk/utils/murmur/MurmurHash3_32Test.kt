package com.crobox.sdk.utils.murmur

import org.junit.Assert.assertEquals
import org.junit.Test

class MurmurHash3_32Test {

    @Test
    fun intToBase36() {
        val parameter1 = 663876300
        val result1 = MurmurHash3_32.intToBase36(parameter1)
        assertEquals("az968c", result1)

        val parameter2 = -1053595020
        val result2 = MurmurHash3_32.intToBase36(parameter2)
        assertEquals("-hfa74c", result2)

        val parameter3 = -96810183
        val result3 = MurmurHash3_32.intToBase36(parameter3)
        assertEquals("-1lmz7r", result3)
    }

    @Test
    fun hash() {
        val testQuery1 = "foobar=bla"
        val result1 = MurmurHash3_32.hash(testQuery1)
        assertEquals("az968c", result1)

        val testQuery2 = "john=doe&foobar=bla"
        val result2 = MurmurHash3_32.hash(testQuery2)
        assertEquals("-hfa74c", result2)

        val testQuery3 = "foobar=bla&john=doe"
        val result3 = MurmurHash3_32.hash(testQuery3)
        assertEquals("-1lmz7r", result3)
    }
}