package net.readian.parcel.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.readian.parcel.R
import net.readian.parcel.feature.login.LoginContract.LoginError
import net.readian.parcel.feature.login.LoginContract.UiState

@Composable
fun LoginScreen(
  onLoginSuccess: () -> Unit,
  viewModel: LoginViewModel,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  LaunchedEffect(Unit) {
    viewModel.uiEvents.collect { event ->
      when (event) {
        LoginContract.UiEvent.NavigateToPackages -> onLoginSuccess()
      }
    }
  }
  LoginContent(
    state = uiState,
    onApiKeyChange = viewModel::onApiKeyChanged,
    onSubmit = { viewModel.validateAndSaveApiKey() },
  )
}

@Composable
fun LoginContent(
  state: UiState,
  onApiKeyChange: (String) -> Unit,
  onSubmit: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val snackBarHostState = androidx.compose.runtime.remember { SnackbarHostState() }

  val errorMessage: String = when (state.error) {
    LoginError.EmptyKey -> stringResource(id = R.string.error_api_key_empty)
    LoginError.InvalidKey -> stringResource(id = R.string.error_invalid_api_key)
    LoginError.Network -> stringResource(id = R.string.error_network_validation)
    LoginError.RateLimited -> stringResource(id = R.string.error_rate_limited)
    null -> ""
  }

  if (state.isError && errorMessage.isNotBlank()) {
    LaunchedEffect(errorMessage) { snackBarHostState.showSnackbar(errorMessage) }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
  ) { paddingValues ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Icon(
        imageVector = Icons.Outlined.Inventory,
        contentDescription = stringResource(id = R.string.app_name),
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(64.dp),
      )
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = stringResource(id = R.string.app_name),
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(bottom = 32.dp),
      )

      PasswordInputField(
        state = state,
        onValueChange = onApiKeyChange,
        onSubmit = onSubmit,
        errorMessage = errorMessage,
      )

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = onSubmit,
        enabled = !state.isLoading && state.apiKey.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
      ) {
        if (state.isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp,
          )
        } else {
          Text(stringResource(id = R.string.login))
        }
      }
    }
  }
}

@Composable
private fun PasswordInputField(
  state: UiState,
  onValueChange: (String) -> Unit,
  onSubmit: () -> Unit,
  errorMessage: String?,
) {
  val keyboardController = LocalSoftwareKeyboardController.current
  var passwordVisible by remember { mutableStateOf(false) }

  OutlinedTextField(
    value = state.apiKey,
    onValueChange = onValueChange,
    label = { Text(stringResource(id = R.string.api_key)) },
    placeholder = { Text(stringResource(id = R.string.enter_api_key)) },
    visualTransformation = if (passwordVisible) {
      VisualTransformation.None
    } else {
      PasswordVisualTransformation()
    },
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    keyboardActions = KeyboardActions(
      onDone = {
        keyboardController?.hide()
        onSubmit()
      },
    ),
    isError = state.isError,
    supportingText = errorMessage?.let { msg -> { Text(msg) } },
    trailingIcon = {
      PasswordVisibilityButton(passwordVisible) {
        passwordVisible = !passwordVisible
      }
    },
    modifier = Modifier.fillMaxWidth(),
    maxLines = 1,
  )
}

@Composable
private fun PasswordVisibilityButton(passwordVisible: Boolean, onClick: () -> Unit) {
  val contentDesc = if (passwordVisible) {
    stringResource(id = R.string.hide_password)
  } else {
    stringResource(id = R.string.show_password)
  }
  IconButton(onClick = onClick) {
    Icon(
      imageVector = if (passwordVisible) {
        Icons.Filled.VisibilityOff
      } else {
        Icons.Filled.Visibility
      },
      contentDescription = contentDesc,
    )
  }
}
