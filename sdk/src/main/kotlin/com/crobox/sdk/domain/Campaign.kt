package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName

/**
 * Represents an ongoing Campaign
 *
 * @property id Campaign ID
 * @property name Campaign Name
 * @property variantId Id of the Campaign Variant
 * @property variantName Name of the Campaign Variant
 * @property control Indicates if variant is allocated to the control group
 */
class Campaign {
    @SerializedName("id") val id: String? = null
    @SerializedName("name") val name: String? = null
    @SerializedName("variantId")val variantId: String? = null
    @SerializedName("variantName")val variantName: String? = null
    @SerializedName("control")val control: Boolean? = null
}