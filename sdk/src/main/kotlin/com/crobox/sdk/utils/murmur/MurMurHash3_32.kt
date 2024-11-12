package com.crobox.sdk.utils.murmur

import com.google.common.hash.Hashing

object MurmurHash3_32 {

    fun intToBase36(number: Int): String {
        if (number == 0) return "0"

        val base = 36
        val characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        var num = number
        val result = StringBuilder()

        if (number < 0) {
            num = number * (-1)
        }

        while (num > 0) {
            val remainder = num % base
            result.append(characters[remainder])
            num /= base
        }

        if (number < 0) {
            result.append("-")
        }

        return result.reverse().toString()
    }

    fun hash(string: String): String {
        val hashing = Hashing.murmur3_32_fixed()
        val stringToByteArray = string.toByteArray()
        val result = hashing.hashBytes(stringToByteArray)
        val intResult = result.asInt()
        val base36 = intToBase36(intResult)

        println("For data $string: result $result intResult: $intResult base36: $base36")
        return base36
    }
}
