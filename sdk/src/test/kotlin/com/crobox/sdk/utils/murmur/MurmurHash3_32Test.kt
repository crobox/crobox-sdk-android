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

    @Test
    fun murmur3MatchesGuavaReferenceValues() {
        // Expected values produced by Guava's Hashing.murmur3_32_fixed().hashBytes(..).asInt(),
        // covering every tail length (0-3 trailing bytes) and multi-byte UTF-8 input.
        val vectors = mapOf(
            "" to 0,
            "a" to 1009084850,
            "ab" to -1681926305,
            "abc" to -1277324294,
            "abcd" to 1139631978,
            "test" to -1167338989,
            "Hello, world!" to -1070186941,
            "The quick brown fox jumps over the lazy dog" to 776992547,
            "héllo wörld ✓ 😀" to 2126341468,
        )
        for ((input, expected) in vectors) {
            assertEquals(input, expected, MurmurHash3_32.murmur3_32(input.toByteArray(Charsets.UTF_8)))
        }
    }
}