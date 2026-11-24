package com.crobox.sdk.presenter

import com.crobox.sdk.domain.CroboxError
import com.crobox.sdk.domain.PromotionsResponse

interface PromotionCallback {
    fun onPromotions(response: PromotionsResponse) {}
    fun onError(error: CroboxError)
}
