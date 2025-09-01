package net.readian.parcel.core.common.di.qualifiers

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import net.readian.parcel.core.common.DispatcherProvider
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

@InstallIn(SingletonComponent::class)
@Module
object CoroutineScopesModule {
  @Singleton
  @ApplicationScope
  @Provides
  fun providesCoroutineScope(dispatcherProvider: DispatcherProvider): CoroutineScope =
    CoroutineScope(SupervisorJob() + dispatcherProvider.default())
}
