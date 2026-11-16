package com.crobox.sdk.common

import android.util.Log

internal object CroboxDebug {

    private const val TAG = "croboxSDK"
    var loggingEnabled = false

    private fun printText(text: String) {
        if (loggingEnabled) {
            Log.d(TAG, text)
        }
    }

    fun promotionError(msg: String) {
        printError("[Promotion]: $msg")
    }

    fun eventError(msg: String) {
        printError("[Event]: $msg")
    }

    private fun printError(msg: String) {
        printText("[Error]: $msg")
    }
}
