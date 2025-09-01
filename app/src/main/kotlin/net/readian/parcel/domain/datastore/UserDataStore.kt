package net.readian.parcel.domain.datastore

import kotlinx.coroutines.flow.Flow
import net.readian.parcel.domain.model.User

interface UserDataStore {
  val userData: Flow<User>

  suspend fun setLoggedIn(loggedIn: Boolean)

  suspend fun logout()
}
