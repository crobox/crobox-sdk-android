package com.crobox.sdk.presenter

interface SocketView  {
    fun onSocketSuccess(dictionary: Map<String, String>) {}
    fun onError(msg: String?)
}
