package com.crobox.sdk.utils.murmur

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

        return result.reverse().toString().lowercase()
    }

    fun hash(string: String): String = intToBase36(murmur3_32(string.toByteArray(Charsets.UTF_8)))

    /** MurmurHash3 x86 32-bit, matching Guava's `Hashing.murmur3_32_fixed().hashBytes(data).asInt()`. */
    internal fun murmur3_32(data: ByteArray, seed: Int = 0): Int {
        val c1 = 0xcc9e2d51.toInt()
        val c2 = 0x1b873593
        var h1 = seed
        val blocks = data.size / 4

        for (i in 0 until blocks) {
            val o = i * 4
            var k1 = (data[o].toInt() and 0xff) or
                ((data[o + 1].toInt() and 0xff) shl 8) or
                ((data[o + 2].toInt() and 0xff) shl 16) or
                ((data[o + 3].toInt() and 0xff) shl 24)
            k1 *= c1
            k1 = k1.rotateLeft(15)
            k1 *= c2
            h1 = h1 xor k1
            h1 = h1.rotateLeft(13)
            h1 = h1 * 5 + 0xe6546b64.toInt()
        }

        val tail = blocks * 4
        var k1 = 0
        val remaining = data.size and 3
        if (remaining >= 3) k1 = k1 xor ((data[tail + 2].toInt() and 0xff) shl 16)
        if (remaining >= 2) k1 = k1 xor ((data[tail + 1].toInt() and 0xff) shl 8)
        if (remaining >= 1) {
            k1 = k1 xor (data[tail].toInt() and 0xff)
            k1 *= c1
            k1 = k1.rotateLeft(15)
            k1 *= c2
            h1 = h1 xor k1
        }

        h1 = h1 xor data.size
        h1 = h1 xor (h1 ushr 16)
        h1 *= 0x85ebca6b.toInt()
        h1 = h1 xor (h1 ushr 13)
        h1 *= 0xc2b2ae35.toInt()
        h1 = h1 xor (h1 ushr 16)
        return h1
    }
}
