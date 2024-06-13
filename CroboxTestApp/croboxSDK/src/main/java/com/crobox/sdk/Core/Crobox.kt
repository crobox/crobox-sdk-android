package com.crobox.sdk.Core

import com.crobox.sdk.Data.Model.EventType
import com.crobox.sdk.Data.Model.RequestQueryParams
import com.crobox.sdk.Presenter.CroboxAPIPresenter
import com.crobox.sdk.Presenter.PromotionView

class Crobox {

    private  val presenter = CroboxAPIPresenter()

    private object InstanceHolder {
        var instance = Crobox()
    }

    companion object {
        val instance: Crobox
            get() = InstanceHolder.instance
    }

    var isDebug = false
    var containerId: String = ""

    fun promotions(queryParams: RequestQueryParams,
                   promotionView: PromotionView){
        presenter.promotions(promotionView = promotionView, params = queryParams)
    }
}

