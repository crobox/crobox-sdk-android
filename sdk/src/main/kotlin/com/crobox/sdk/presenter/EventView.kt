package com.crobox.sdk.presenter

interface EventView  {
    fun onSuccess(dictionary: Map<String, String>) {}
    fun onError(msg: String?)
}
