package net.readian.parcel.data.repository

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.security.GeneralSecurityException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyRepository @Inject constructor(
  @param:ApplicationContext private val context: Context,
) {
  private val aead: Aead by lazy {
    AeadConfig.register()
    val keysetHandle = AndroidKeysetManager.Builder()
      .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
      .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
      .withMasterKeyUri(MASTER_KEY_URI)
      .build()
      .keysetHandle
    keysetHandle.getPrimitive(Aead::class.java)
  }

  suspend fun setApiKey(apiKey: String) {
    val sanitized = apiKey
      .replace("\r", "")
      .replace("\n", "")
      .trim()
    val encrypted = encrypt(sanitized)
    context.apiKeyDataStore.edit { preferences ->
      preferences[API_KEY_PREF] = encrypted
    }
  }

  suspend fun getApiKey(): String? {
    val encrypted = context.apiKeyDataStore.data
      .map { preferences -> preferences[API_KEY_PREF] }
      .first()

    if (encrypted.isNullOrBlank()) return null

    return decrypt(encrypted)
      ?.replace("\r", "")
      ?.replace("\n", "")
      ?.trim()
      ?.takeIf { it.isNotBlank() }
  }

  suspend fun hasApiKey(): Boolean = !getApiKey().isNullOrBlank()

  suspend fun clearApiKey() {
    context.apiKeyDataStore.edit { preferences ->
      preferences.remove(API_KEY_PREF)
    }
  }

  private fun encrypt(plaintext: String): String {
    val ciphertext = aead.encrypt(plaintext.toByteArray(Charsets.UTF_8), null)
    return Base64.encodeToString(ciphertext, Base64.NO_WRAP)
  }

  private fun decrypt(ciphertext: String): String? = try {
    val decoded = Base64.decode(ciphertext, Base64.NO_WRAP)
    val plaintext = aead.decrypt(decoded, null)
    String(plaintext, Charsets.UTF_8)
  } catch (e: GeneralSecurityException) {
    Timber.e(e, "Error decrypting API key")
    null
  } catch (e: IllegalArgumentException) {
    Timber.e(e, "Error decoding Base64")
    null
  }

  companion object {
    private val API_KEY_PREF = stringPreferencesKey("api_key")
    private const val KEYSET_NAME = "api_key_keyset"
    private const val PREF_FILE_NAME = "api_key_keyset_prefs"
    private const val MASTER_KEY_URI = "android-keystore://api_key_master_key"
  }
}

private val Context.apiKeyDataStore: DataStore<Preferences> by preferencesDataStore("api_key_prefs")
