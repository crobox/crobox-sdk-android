package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName

/**
 * The context about campaigns
 *
 * @property experiments The list of ongoing campaigns
 * @property sessionId Session ID
 * @property visitorId Visitor ID
 * @property groupName List of campaign and variant names, combined
 *
 */
class PromotionContext {
    @SerializedName("experiments") val experiments:  List<Experiment>? = null
    @SerializedName("sid") val sessionId: String? = null
    @SerializedName("pid") val visitorId: String? = null
    @SerializedName("groupName") val groupName: String? = null
}