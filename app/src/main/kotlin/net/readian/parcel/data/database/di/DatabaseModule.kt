package net.readian.parcel.data.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import net.readian.parcel.BuildConfig
import net.readian.parcel.data.database.ParcelDatabase
import net.readian.parcel.data.database.dao.CarrierDao
import net.readian.parcel.data.database.dao.PackageDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun provideParcelDatabase(
    @ApplicationContext context: Context,
  ): ParcelDatabase {
    val builder = Room.databaseBuilder(
      context,
      ParcelDatabase::class.java,
      ParcelDatabase.Companion.DATABASE_NAME,
    )
    if (BuildConfig.DEBUG) {
      builder.fallbackToDestructiveMigration()
    }
    return builder.build()
  }

  @Provides
  fun providePackageDao(database: ParcelDatabase): PackageDao {
    return database.packageDao()
  }

  @Provides
  fun provideCarrierDao(database: ParcelDatabase): CarrierDao {
    return database.carrierDao()
  }

  @Provides
  @Singleton
  fun provideJson(): Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
  }
}
