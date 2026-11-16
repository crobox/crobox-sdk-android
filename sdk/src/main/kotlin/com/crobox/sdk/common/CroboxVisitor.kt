package com.crobox.sdk.common

import android.content.Context
import java.util.UUID

/**
 * Keeps the visitorId on the device, so the same person is the same visitor across app launches.
 *
 * Crobox assigns campaign variants and the control group per visitor. A visitorId that changes on every launch
 * re-buckets the user each time and makes A/B results meaningless.
 *
 * ```
 * Crobox.getInstance(
 *     CroboxConfig(containerId = "xlrc9t", visitorId = CroboxVisitor.visitorId(context))
 * )
 * ```
 */
object CroboxVisitor {

    private const val PREFERENCES = "com.crobox.sdk"
    private const val VISITOR_ID = "visitorId"

    /**
     * Returns the stored visitorId, creating and storing a new one on first use.
     */
    @JvmStatic
    fun visitorId(context: Context): UUID = synchronized(this) {
        val preferences = context.applicationContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        preferences.getString(VISITOR_ID, null)
            ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            ?: UUID.randomUUID().also { preferences.edit().putString(VISITOR_ID, it.toString()).apply() }
    }

    /**
     * Replaces the stored visitorId with a new one and returns it, for example when the user logs out and the next
     * person on the device should be a new visitor. Pass it to [com.crobox.sdk.core.Crobox.updateConfig].
     */
    @JvmStatic
    fun reset(context: Context): UUID = synchronized(this) {
        UUID.randomUUID().also {
            context.applicationContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit().putString(VISITOR_ID, it.toString()).apply()
        }
    }
}
