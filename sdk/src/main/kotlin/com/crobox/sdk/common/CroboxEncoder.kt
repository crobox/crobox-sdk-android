package com.crobox.sdk.common

import android.util.Base64
import java.math.BigInteger
import java.util.UUID

object CroboxEncoder {

    /**
     *  Example: lu9znf91 for 1711554991093
     */
    fun toBase36(millis: Long): String {
        return BigInteger(millis.toString()).toString(36)
    }

    fun toBase64(uuid: UUID): String {
        return Base64.encodeToString(uuid.toString().toByteArray(), Base64.DEFAULT)
    }
}