package net.readian.parcel.domain.usecase

import net.readian.parcel.domain.datastore.UserDataStore
import net.readian.parcel.domain.repository.PackageRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
  private val packageRepository: PackageRepository,
  private val userDataStore: UserDataStore,
) {
  suspend operator fun invoke() {
    packageRepository.clearSavedApiKey()
    userDataStore.logout()
  }
}
