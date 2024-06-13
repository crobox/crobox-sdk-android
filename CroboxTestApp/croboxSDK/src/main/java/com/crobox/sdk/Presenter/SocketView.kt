package com.crobox.sdk.Presenter

interface SocketView  {
    fun onSocketSuccess(dictionary: Map<String, String>) {}
    fun onError(msg: String?)
}
