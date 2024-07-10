package com.crobox.sdk.common

import java.math.BigInteger

internal object CroboxEncoder {

    /**
     *  Example: lu9znf91 for 1711554991093
     */
    fun toBase36(millis: Long): String {
        return BigInteger(millis.toString()).toString(36)
    }

}