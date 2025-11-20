package net.readian.parcel.domain.usecase

import io.kotest.core.spec.style.FunSpec
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import net.readian.parcel.domain.datastore.UserDataStore
import net.readian.parcel.domain.repository.PackageRepository

class LogoutUseCaseTest : FunSpec() {
  @MockK
  private lateinit var packageRepository: PackageRepository

  @MockK
  private lateinit var userDataStore: UserDataStore

  private lateinit var useCase: LogoutUseCase

  init {
    beforeTest {
      MockKAnnotations.init(this@LogoutUseCaseTest)
      useCase = LogoutUseCase(packageRepository, userDataStore)
    }

    test("should clear API key and logout on invoke") {
      runTest {
        coEvery { packageRepository.clearSavedApiKey() } returns Unit
        coEvery { userDataStore.logout() } returns Unit

        useCase()

        coVerify { packageRepository.clearSavedApiKey() }
        coVerify { userDataStore.logout() }
      }
    }
  }
}
