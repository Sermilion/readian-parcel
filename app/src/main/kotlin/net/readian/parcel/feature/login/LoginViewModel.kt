package net.readian.parcel.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.readian.parcel.data.datastore.ReadianUserDataStore
import net.readian.parcel.domain.repository.PackageRepository
import net.readian.parcel.feature.login.LoginContract.LoginError
import net.readian.parcel.feature.login.LoginContract.UiState
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
  private val userDataStore: ReadianUserDataStore,
  private val packageRepository: PackageRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(UiState())
  val uiState: StateFlow<UiState> = _uiState.asStateFlow()

  fun onApiKeyChanged(apiKey: String) {
    _uiState.update {
      it.copy(
        apiKey = apiKey,
        isError = false,
        error = null,
      )
    }
  }

  fun validateAndSaveApiKey(onSuccess: () -> Unit) {
    if (_uiState.value.apiKey.isBlank()) {
      _uiState.update {
        it.copy(
          isError = true,
          error = LoginError.EmptyKey,
        )
      }
      return
    }

    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      runCatching {
        packageRepository.validateAndSaveApiKey(_uiState.value.apiKey)
      }.onSuccess { isValid ->
        if (isValid) {
          userDataStore.setLoggedIn(true)
          onSuccess()
        } else {
          userDataStore.logout()
          _uiState.update {
            it.copy(
              isLoading = false,
              isError = true,
              error = LoginError.InvalidKey,
            )
          }
        }
      }.onFailure { e ->
        userDataStore.logout()
        _uiState.update {
          it.copy(
            isLoading = false,
            isError = true,
            error = LoginError.Network,
          )
        }
        Timber.e(e, "Error validating API key")
      }
    }
  }
}
