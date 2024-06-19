package com.crobox.sdk.Domain

import com.google.gson.annotations.SerializedName

class Experiment {
    @SerializedName("id") val id: String? = null
    @SerializedName("name") val name: String? = null
    @SerializedName("variantId")val variantId: String? = null
    @SerializedName("variantName")val variantName: String? = null
    @SerializedName("control")val control: Boolean? = null
}