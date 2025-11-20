package net.readian.parcel.feature.packages

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import net.readian.parcel.core.common.ratelimit.RefreshManager
import net.readian.parcel.core.common.ratelimit.RefreshState
import net.readian.parcel.domain.repository.CarrierRepository
import net.readian.parcel.domain.repository.PackageRepository
import net.readian.parcel.domain.usecase.LogoutUseCase
import net.readian.parcel.feature.packages.PackagesContract.PackagesUiState

@OptIn(ExperimentalCoroutinesApi::class)
class PackagesViewModelTest : FunSpec() {
  private val testDispatcher = UnconfinedTestDispatcher()

  @MockK
  private lateinit var packageRepository: PackageRepository

  @MockK
  private lateinit var carrierRepository: CarrierRepository

  @MockK
  private lateinit var refreshManager: RefreshManager

  @MockK
  private lateinit var logoutUseCase: LogoutUseCase

  private lateinit var viewModel: PackagesViewModel

  init {
    beforeSpec {
      Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
      Dispatchers.resetMain()
    }

    beforeTest {
      MockKAnnotations.init(this@PackagesViewModelTest)
      coEvery { carrierRepository.refresh() } returns Unit
      coEvery { refreshManager.syncCooldownFromRepo() } returns Unit
      coEvery { refreshManager.performRefresh() } returns Unit
      every { carrierRepository.carriers } returns flowOf(emptyMap())
      every { refreshManager.refreshState } returns MutableStateFlow(RefreshState())
    }

    test("initial state should be loading") {
      every { packageRepository.getAllPackages() } returns flowOf(emptyList())

      viewModel = PackagesViewModel(
        packageRepository,
        carrierRepository,
        refreshManager,
        logoutUseCase,
      )

      viewModel.uiState.value.shouldBeTypeOf<PackagesUiState.Loading>()
    }

    test("refresh packages should call refresh manager") {
      every { packageRepository.getAllPackages() } returns flowOf(emptyList())

      viewModel = PackagesViewModel(
        packageRepository,
        carrierRepository,
        refreshManager,
        logoutUseCase,
      )

      viewModel.refreshPackages()
      testDispatcher.scheduler.advanceUntilIdle()

      coVerify(atLeast = 2) { refreshManager.performRefresh() }
    }

    test("dismiss error should call refresh manager") {
      every { packageRepository.getAllPackages() } returns flowOf(emptyList())
      every { refreshManager.resetErrorState() } returns Unit

      viewModel = PackagesViewModel(
        packageRepository,
        carrierRepository,
        refreshManager,
        logoutUseCase,
      )

      viewModel.onDismissError()

      coVerify { refreshManager.resetErrorState() }
    }
  }
}
