package net.readian.parcel.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.readian.parcel.data.proto.UserDataOuterClass.UserData
import net.readian.parcel.domain.datastore.UserDataStore
import net.readian.parcel.domain.model.User
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadianUserDataStore @Inject constructor(
  private val dataStore: DataStore<UserData>,
) : UserDataStore {
  override val userData: Flow<User> = dataStore.data.map { protoData ->
    User(
      isLoggedIn = protoData.isLoggedIn,
    )
  }

  override suspend fun setLoggedIn(loggedIn: Boolean) {
    dataStore.updateData { userData ->
      userData.toBuilder()
        .setIsLoggedIn(loggedIn)
        .build()
    }
  }

  override suspend fun logout() {
    dataStore.updateData { userData ->
      userData.toBuilder()
        .setIsLoggedIn(false)
        .build()
    }
  }
}

object UserDataSerializer : Serializer<UserData> {
  override val defaultValue: UserData = UserData.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): UserData = try {
    UserData.parseFrom(input)
  } catch (exception: InvalidProtocolBufferException) {
    throw CorruptionException("Cannot read proto.", exception)
  }

  override suspend fun writeTo(t: UserData, output: OutputStream) = t.writeTo(output)
}
