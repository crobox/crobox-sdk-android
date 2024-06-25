package com.crobox.sdk.common

import android.util.Log
import com.crobox.sdk.core.Crobox

class CroboxDebug {

    private object InstanceHolder {
        val instance = CroboxDebug()
    }

    companion object {
        val instance: CroboxDebug
            get() = InstanceHolder.instance
    }

    fun printText(text: String) {
        if (Crobox.instance.isDebug) {
            Log.d("croboxSDK LOG", "croboxSDK LOG: $text")
        }
    }

    fun printParams(params: Map<String, Any>) {
        if (Crobox.instance.isDebug) {
            Log.d("croboxSDK LOG", "croboxSDK LOG: $params")
        }
    }
}