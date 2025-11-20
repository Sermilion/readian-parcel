package net.readian.parcel.data.repository

import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import java.io.File

class ApiKeyRepositoryTest :
  FunSpec({
    val tempDir = tempdir()
    lateinit var dataStore: DataStore<Preferences>

    beforeTest {
      dataStore = PreferenceDataStoreFactory.create(
        produceFile = { File(tempDir, "test_api_key_${System.nanoTime()}.preferences_pb") },
      )

      mockkStatic(Base64::class)
      every { Base64.encodeToString(any(), any()) } answers {
        java.util.Base64.getEncoder().encodeToString(firstArg())
      }
      every { Base64.decode(any<String>(), any()) } answers {
        java.util.Base64.getDecoder().decode(firstArg<String>())
      }
    }

    afterTest {
      unmockkStatic(Base64::class)
    }

    test("setApiKey and getApiKey should store and retrieve API key") {
      runTest {
        val apiKeyPref = stringPreferencesKey("api_key")

        dataStore.edit { preferences ->
          preferences[apiKeyPref] = "test-api-key-123"
        }

        val result = dataStore.data
          .map { preferences -> preferences[apiKeyPref] }
          .first()

        result shouldBe "test-api-key-123"
      }
    }

    test("getApiKey should return null when no key is stored") {
      runTest {
        val apiKeyPref = stringPreferencesKey("api_key")

        val result = dataStore.data
          .map { preferences -> preferences[apiKeyPref] }
          .first()

        result.shouldBeNull()
      }
    }

    test("clearApiKey should remove stored API key") {
      runTest {
        val apiKeyPref = stringPreferencesKey("api_key")

        dataStore.edit { preferences ->
          preferences[apiKeyPref] = "test-key"
        }

        dataStore.edit { preferences ->
          preferences.remove(apiKeyPref)
        }

        val result = dataStore.data
          .map { preferences -> preferences[apiKeyPref] }
          .first()

        result.shouldBeNull()
      }
    }

    test("setApiKey should sanitize input by removing newlines and trimming") {
      runTest {
        val input = "  test-key\r\n  "
        val sanitized = input
          .replace("\r", "")
          .replace("\n", "")
          .trim()

        sanitized shouldBe "test-key"
      }
    }

    test("getApiKey should return null for blank keys") {
      runTest {
        val input = "   "
        val sanitized = input
          .replace("\r", "")
          .replace("\n", "")
          .trim()
        val result = sanitized.takeIf { it.isNotBlank() }

        result.shouldBeNull()
      }
    }

    test("setApiKey should overwrite existing key") {
      runTest {
        val apiKeyPref = stringPreferencesKey("api_key")

        dataStore.edit { preferences ->
          preferences[apiKeyPref] = "first-key"
        }

        dataStore.edit { preferences ->
          preferences[apiKeyPref] = "second-key"
        }

        val result = dataStore.data
          .map { preferences -> preferences[apiKeyPref] }
          .first()

        result shouldBe "second-key"
      }
    }
  })
