package net.readian.parcel.domain.usecase

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import net.readian.parcel.domain.datastore.UserDataStore
import net.readian.parcel.domain.model.ApiValidationResult
import net.readian.parcel.domain.model.RateLimitException
import net.readian.parcel.domain.repository.PackageRepository

class ValidateAndLoginUseCaseTest : FunSpec() {
  @MockK
  private lateinit var packageRepository: PackageRepository

  @MockK
  private lateinit var userDataStore: UserDataStore

  private lateinit var useCase: ValidateAndLoginUseCase

  init {
    beforeTest {
      MockKAnnotations.init(this@ValidateAndLoginUseCaseTest)
      useCase = ValidateAndLoginUseCase(packageRepository, userDataStore)
    }

    test("should return Success when API key is valid") {
      runTest {
        coEvery { packageRepository.validateAndSaveApiKey("valid-key") } returns ApiValidationResult.Success
        coEvery { userDataStore.setLoggedIn(true) } returns Unit

        val result = useCase("valid-key")

        result shouldBe ApiValidationResult.Success
        coVerify { userDataStore.setLoggedIn(true) }
      }
    }

    test("should return InvalidKey when API key is invalid") {
      runTest {
        coEvery { packageRepository.validateAndSaveApiKey("invalid-key") } returns ApiValidationResult.InvalidKey
        coEvery { userDataStore.logout() } returns Unit

        val result = useCase("invalid-key")

        result shouldBe ApiValidationResult.InvalidKey
        coVerify { userDataStore.logout() }
      }
    }

    test("should return RateLimited when rate limit is exceeded") {
      runTest {
        val exception = RateLimitException(60000L, 0)
        coEvery { packageRepository.validateAndSaveApiKey("key") } returns ApiValidationResult.RateLimited(exception)

        val result = useCase("key")

        result shouldBe ApiValidationResult.RateLimited(exception)
      }
    }

    test("should return NetworkError on network failure") {
      runTest {
        coEvery { packageRepository.validateAndSaveApiKey("key") } returns ApiValidationResult.NetworkError
        coEvery { userDataStore.logout() } returns Unit

        val result = useCase("key")

        result shouldBe ApiValidationResult.NetworkError
        coVerify { userDataStore.logout() }
      }
    }
  }
}
