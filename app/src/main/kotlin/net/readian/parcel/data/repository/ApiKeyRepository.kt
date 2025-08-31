package net.readian.parcel.data.repository

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "parcel_api_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun setApiKey(apiKey: String) {
        val sanitized = apiKey
            .replace("\r", "")
            .replace("\n", "")
            .trim()
        encryptedPrefs.edit { putString(API_KEY_PREF, sanitized) }
    }

    fun getApiKey(): String? {
        val raw = encryptedPrefs.getString(API_KEY_PREF, null)
        val sanitized = raw
            ?.replace("\r", "")
            ?.replace("\n", "")
            ?.trim()
        return sanitized?.takeIf { !it.isNullOrBlank() }
    }

    fun hasApiKey(): Boolean {
        return !getApiKey().isNullOrBlank()
    }

    fun clearApiKey() {
        encryptedPrefs.edit { remove(API_KEY_PREF) }
    }

    companion object Companion {
        private const val API_KEY_PREF = "api_key"
    }
}
