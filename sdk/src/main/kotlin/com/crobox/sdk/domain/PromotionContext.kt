package com.crobox.sdk.domain

import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * The context about campaigns
 */
data class PromotionContext(
    /**
     * The list of ongoing campaigns
     */
    @SerializedName("experiments")
    val campaigns: List<Campaign>,

    /**
     * Session ID
     */
    @SerializedName("sid")
    val sessionId: UUID,

    /**
     * Visitor ID
     */
    @SerializedName("pid")
    val visitorId: UUID,

    /**
     * The list of campaign and variant names, combined
     */
    @SerializedName("groupName")
    val groupName: String?
)
