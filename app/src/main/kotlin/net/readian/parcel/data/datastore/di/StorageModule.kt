package net.readian.parcel.data.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.readian.parcel.data.datastore.ReadianUserDataStore
import net.readian.parcel.data.datastore.UserDataSerializer
import net.readian.parcel.data.proto.UserDataOuterClass
import net.readian.parcel.domain.datastore.UserDataStore
import javax.inject.Singleton

@Module(includes = [StorageModule.Bindings::class])
@InstallIn(SingletonComponent::class)
object StorageModule {

  @Provides
  @Singleton
  fun providePreferencesDataStore(
    @ApplicationContext context: Context,
  ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
    produceFile = { context.preferencesDataStoreFile("parcel_prefs") },
  )

  @Provides
  @Singleton
  fun provideUserDataStore(
    @ApplicationContext context: Context,
  ): DataStore<UserDataOuterClass.UserData> = DataStoreFactory.create(
    serializer = UserDataSerializer,
    produceFile = { context.dataStoreFile("user_data.pb") },
  )

  @Module
  @InstallIn(SingletonComponent::class)
  interface Bindings {

    @Binds
    @Singleton
    fun bindUserDataStore(store: ReadianUserDataStore): UserDataStore
  }
}
