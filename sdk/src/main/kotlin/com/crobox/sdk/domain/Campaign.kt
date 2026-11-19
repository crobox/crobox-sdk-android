package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName

/**
 * Represents an ongoing Campaign
 */
data class Campaign (
    /**
     * Campaign ID
     */
    @SerializedName("id")
    val id: Int,

    /**
     * Campaign Name
     */
    @SerializedName("name")
    val name: String,

    /**
     * Id of the Campaign Variant
     */
    @SerializedName("variantId")
    val variantId: Int,

    /**
     * Name of the Campaign Variant
     */
    @SerializedName("variantName")
    val variantName: String,

    /**
     * Indicates if variant is allocated to the control group
     */
    @SerializedName("control")
    val control: Boolean
)
