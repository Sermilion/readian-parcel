package net.readian.parcel.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import net.readian.parcel.domain.repository.ApiKeyRepository
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class ApiKeyRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ApiKeyRepository {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        "parcel_api_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun setApiKey(apiKey: String) {
        encryptedPrefs.edit { putString(API_KEY_PREF, apiKey) }
    }

    override fun getApiKey(): String? {
        return encryptedPrefs.getString(API_KEY_PREF, null)
    }

    override fun hasApiKey(): Boolean {
        return !getApiKey().isNullOrBlank()
    }

    override fun clearApiKey() {
        encryptedPrefs.edit { remove(API_KEY_PREF) }
    }

    companion object {
        private const val API_KEY_PREF = "api_key"
    }
}