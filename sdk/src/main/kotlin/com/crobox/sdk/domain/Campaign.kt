package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName

/**
 * Represents an ongoing Campaign
 */
class Campaign {
    /**
     * Campaign ID
     */
    @SerializedName("id")
    val id: String? = null

    /**
     * Campaign Name
     */
    @SerializedName("name")
    val name: String? = null

    /**
     * Id of the Campaign Variant
     */
    @SerializedName("variantId")
    val variantId: String? = null

    /**
     * Name of the Campaign Variant
     */
    @SerializedName("variantName")
    val variantName: String? = null

    /**
     * Indicates if variant is allocated to the control group
     */
    @SerializedName("control")
    val control: Boolean? = null
}