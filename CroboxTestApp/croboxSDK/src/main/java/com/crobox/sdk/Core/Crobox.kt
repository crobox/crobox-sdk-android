package com.crobox.sdk.Core

class Crobox {

    private object InstanceHolder {
        var instance = Crobox()
    }

    companion object {
        val instance: Crobox
            get() = InstanceHolder.instance
    }

    var isDebug = false
    var containerId: String = ""
}

