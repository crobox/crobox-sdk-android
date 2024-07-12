package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * The context about campaigns
 */
class PromotionContext {
    /**
     * The list of ongoing campaigns
     */
    @SerializedName("experiments")
    val campaigns: List<Campaign>? = null

    /**
     * Session ID
     */
    @SerializedName("sid")
    val sessionId: UUID? = null

    /**
     * Visitor ID
     */
    @SerializedName("pid")
    val visitorId: UUID? = null

    /**
     * The list of campaign and variant names, combined
     */
    @SerializedName("groupName")
    val groupName: String? = null
}