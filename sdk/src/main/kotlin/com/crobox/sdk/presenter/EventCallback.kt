package com.crobox.sdk.presenter

interface EventCallback  {
    fun onSuccess(dictionary: Map<String, String>) {}
    fun onError(msg: String?)
}
