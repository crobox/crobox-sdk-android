package com.crobox.sdk.common

import android.util.Log

class CroboxDebug {

    var isDebug = false

    private object InstanceHolder {
        val instance = CroboxDebug()
    }

    companion object {
        val instance: CroboxDebug
            get() = InstanceHolder.instance
    }

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
}