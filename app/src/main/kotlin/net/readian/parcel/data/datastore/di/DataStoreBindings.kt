package net.readian.parcel.data.datastore.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.readian.parcel.data.datastore.ReadianUserDataStore
import net.readian.parcel.domain.datastore.UserDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataStoreBindings {

    @Binds
    @Singleton
    fun bindUserDataStore(store: ReadianUserDataStore): UserDataStore
}
