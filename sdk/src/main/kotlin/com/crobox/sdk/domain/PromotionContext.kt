package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * The context about campaigns
 *
 * @property campaigns The list of ongoing campaigns
 * @property sessionId Session ID
 * @property visitorId Visitor ID
 * @property groupName List of campaign and variant names, combined
 *
 */
class PromotionContext {
    @SerializedName("experiments")
    val campaigns: List<Campaign>? = null

    @SerializedName("sid")
    val sessionId: UUID? = null

    @SerializedName("pid")
    val visitorId: UUID? = null

    @SerializedName("groupName")
    val groupName: String? = null
}