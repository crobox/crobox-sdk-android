package com.crobox.sdk.Presenter

import com.crobox.sdk.Domian.PromotionsResponse

interface PromotionView  {
    fun onPromotions(promotionsResponse: PromotionsResponse?) {}
    fun onError(msg: String?)
    fun onSuccess(msg: String?) {}
}