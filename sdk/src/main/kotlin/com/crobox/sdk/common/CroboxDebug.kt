package com.crobox.sdk.common

import android.util.Log

object CroboxDebug {

    var isDebug = false

    fun printText(text: String) {
        if (isDebug) {
            Log.d("croboxSDK LOG", "croboxSDK LOG: $text")
        }
    }

    fun printParams(params: Map<String, Any>) {
        if (isDebug) {
            Log.d("croboxSDK LOG", "croboxSDK LOG: $params")
        }
    }

    fun promotionError(msg: String) {
        printError("[Promotion]: $msg")
    }

    fun eventError(msg: String) {
        printError("[Event]: $msg")
    }

    private fun printError(msg: String) {
        if (isDebug) {
            Log.d("croboxSDK Error", "[Error]: $msg")
        }
    }
}