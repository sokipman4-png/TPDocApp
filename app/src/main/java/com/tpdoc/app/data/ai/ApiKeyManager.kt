package com.tpdoc.app.data.ai

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object ApiKeyManager {
    private const val PREFS_NAME = "tpdoc_secure"
    private const val KEY_API_KEY = "openrouter_api_key"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        if (!::prefs.isInitialized) {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            prefs = EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }
    }

    fun getApiKey(): String? = prefs.getString(KEY_API_KEY, null)

    fun setApiKey(key: String) {
        prefs.edit().putString(KEY_API_KEY, key).apply()
    }

    fun removeApiKey() {
        prefs.edit().remove(KEY_API_KEY).apply()
    }

    fun hasApiKey(): Boolean = !getApiKey().isNullOrBlank()

    fun getMaskedKey(): String {
        val key = getApiKey() ?: return ""
        if (key.length <= 8) return "*".repeat(key.length)
        return key.take(8) + "*".repeat(key.length - 8)
    }
}