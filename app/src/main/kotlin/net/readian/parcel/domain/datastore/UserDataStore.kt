package net.readian.parcel.domain.datastore

import kotlinx.coroutines.flow.Flow
import net.readian.parcel.domain.model.UserDataModel

interface UserDataStore {
    val userData: Flow<UserDataModel>

    suspend fun setLoggedIn(loggedIn: Boolean)

    suspend fun logout()
}
