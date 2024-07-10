package com.crobox.sdk.common

import android.util.Log

internal object CroboxDebug {

    val TAG = "croboxSDK"
    var loggingEnabled = false

    fun printText(text: String) {
        if (loggingEnabled) {
            Log.d(TAG, text)
        }
    }

    fun printParams(params: Map<String, Any>) {
        if (loggingEnabled) {
            Log.d(TAG, "$params")
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