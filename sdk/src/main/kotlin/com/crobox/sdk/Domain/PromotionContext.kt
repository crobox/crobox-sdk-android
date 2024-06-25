package com.crobox.sdk.domain
import com.google.gson.annotations.SerializedName

class PromotionContext {
    @SerializedName("experiments") val experiments:  List<Experiment>? = null
    @SerializedName("sid") val sid: String? = null
    @SerializedName("pid") val pid: String? = null
    @SerializedName("groupName") val groupName: String? = null
}