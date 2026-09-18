package com.tpdoc.app.data

import android.content.Context

/**
 * Metadata-app lokaal (SharedPreferences, niet-gevoelig).
 * Gebruikt voor het onboarding flag (STEP 5.A): zien of de gebruiker al
 * de eerste-k eer onboarding heeft afgerond.
 */
object AppMeta {
    private const val PREFS_NAME = "tpdoc_meta"
    private const val KEY_ONBOARDING_DONE = "onboarding_done_v1"

    fun isOnboardingDone(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, 0).getBoolean(KEY_ONBOARDING_DONE, false)

    fun setOnboardingDone(context: Context) {
        context.getSharedPreferences(PREFS_NAME, 0)
            .edit()
            .putBoolean(KEY_ONBOARDING_DONE, true)
            .apply()
    }
}