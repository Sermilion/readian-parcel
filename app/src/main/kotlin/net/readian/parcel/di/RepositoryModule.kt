package net.readian.parcel.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.readian.parcel.data.repository.ApiKeyRepositoryImpl
import net.readian.parcel.data.repository.ReadianPackageRepository
import net.readian.parcel.domain.repository.ApiKeyRepository
import net.readian.parcel.domain.repository.PackageRepository
import javax.inject.Singleton

/**
 * Hilt module for binding repository interfaces to their implementations
 * This maintains clean architecture by allowing the presentation layer
 * to depend only on domain interfaces, not data implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindPackageRepository(
        readianPackageRepository: ReadianPackageRepository
    ): PackageRepository
    
    @Binds
    @Singleton
    abstract fun bindApiKeyRepository(
        apiKeyRepositoryImpl: ApiKeyRepositoryImpl
    ): ApiKeyRepository
}