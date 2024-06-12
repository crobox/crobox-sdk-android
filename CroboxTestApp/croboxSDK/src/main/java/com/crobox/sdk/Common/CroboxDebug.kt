package com.crobox.sdk.Common

import com.crobox.sdk.Core.Crobox

class CroboxDebug {

    private object InstanceHolder {
        var instance = CroboxDebug()
    }

    companion object {
        val instance: Crobox
            get() = CroboxDebug.instance
    }

    var isDebug = false;

    fun printText(text: Any) {
        if (Crobox.instance.isDebug) {
            println(text)
        }
    }

    fun printParams(params: Map<String, Any>) {
        if (Crobox.instance.isDebug) {
            println(params)
        }
    }
}