package net.readian.parcel.feature.login

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import net.readian.parcel.domain.model.ApiValidationResult
import net.readian.parcel.domain.usecase.ValidateAndLoginUseCase
import net.readian.parcel.feature.login.LoginContract.LoginError

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest : FunSpec() {
  private val testDispatcher = UnconfinedTestDispatcher()

  @MockK
  private lateinit var validateAndLoginUseCase: ValidateAndLoginUseCase

  private lateinit var viewModel: LoginViewModel

  init {
    beforeSpec {
      Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
      Dispatchers.resetMain()
    }

    beforeTest {
      MockKAnnotations.init(this@LoginViewModelTest)
      viewModel = LoginViewModel(validateAndLoginUseCase)
    }

    test("initial state should have empty api key and no errors") {
      viewModel.uiState.value.apiKey shouldBe ""
      viewModel.uiState.value.isError shouldBe false
      viewModel.uiState.value.error shouldBe null
      viewModel.uiState.value.isLoading shouldBe false
    }

    test("onApiKeyChanged should update api key and clear errors") {
      viewModel.onApiKeyChanged("new-api-key")

      viewModel.uiState.value.apiKey shouldBe "new-api-key"
      viewModel.uiState.value.isError shouldBe false
      viewModel.uiState.value.error shouldBe null
    }

    test("validateAndSaveApiKey with empty key should show error") {
      viewModel.onApiKeyChanged("")
      viewModel.validateAndSaveApiKey()

      viewModel.uiState.value.isError shouldBe true
      viewModel.uiState.value.error shouldBe LoginError.EmptyKey
      viewModel.uiState.value.isLoading shouldBe false
    }

    test("validateAndSaveApiKey with invalid key should show error") {
      coEvery { validateAndLoginUseCase("invalid-key") } returns ApiValidationResult.InvalidKey

      viewModel.onApiKeyChanged("invalid-key")
      viewModel.validateAndSaveApiKey()

      testDispatcher.scheduler.advanceUntilIdle()

      viewModel.uiState.value.isError shouldBe true
      viewModel.uiState.value.error shouldBe LoginError.InvalidKey
      coVerify { validateAndLoginUseCase("invalid-key") }
    }

    test("validateAndSaveApiKey with rate limit should show error") {
      coEvery { validateAndLoginUseCase("key") } returns ApiValidationResult.RateLimited(mockk())

      viewModel.onApiKeyChanged("key")
      viewModel.validateAndSaveApiKey()

      testDispatcher.scheduler.advanceUntilIdle()

      viewModel.uiState.value.isError shouldBe true
      viewModel.uiState.value.error shouldBe LoginError.RateLimited
    }

    test("validateAndSaveApiKey with network error should show error") {
      coEvery { validateAndLoginUseCase("key") } returns ApiValidationResult.NetworkError

      viewModel.onApiKeyChanged("key")
      viewModel.validateAndSaveApiKey()

      testDispatcher.scheduler.advanceUntilIdle()

      viewModel.uiState.value.isError shouldBe true
      viewModel.uiState.value.error shouldBe LoginError.Network
    }
  }
}
