package com.crobox.sdk.presenter

import com.crobox.sdk.domain.PromotionsResponse

interface PromotionView  {
    fun onPromotions(promotionsResponse: PromotionsResponse?) {}
    fun onError(msg: String?)
    fun onSuccess(msg: String?) {}
}
