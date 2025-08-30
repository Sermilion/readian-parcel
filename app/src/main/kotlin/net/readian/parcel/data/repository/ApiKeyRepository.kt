package net.readian.parcel.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        "parcel_api_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveApiKey(apiKey: String) {
        encryptedPrefs.edit().putString(API_KEY_PREF, apiKey).apply()
    }

    fun getApiKey(): String? {
        return encryptedPrefs.getString(API_KEY_PREF, null)
    }

    fun hasApiKey(): Boolean {
        return !getApiKey().isNullOrBlank()
    }

    fun clearApiKey() {
        encryptedPrefs.edit().remove(API_KEY_PREF).apply()
    }

    companion object {
        private const val API_KEY_PREF = "api_key"
    }
}