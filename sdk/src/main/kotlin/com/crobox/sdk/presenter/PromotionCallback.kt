package com.crobox.sdk.presenter

import com.crobox.sdk.domain.PromotionsResponse

interface PromotionCallback  {
    fun onPromotions(promotionsResponse: PromotionsResponse?) {}
    fun onError(msg: String?)
}
