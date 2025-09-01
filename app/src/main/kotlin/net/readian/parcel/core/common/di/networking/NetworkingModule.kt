package net.readian.parcel.core.common.di.networking

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import net.readian.parcel.BuildConfig
import net.readian.parcel.core.common.di.qualifiers.ApiUrl
import net.readian.parcel.core.common.di.qualifiers.Authenticated
import net.readian.parcel.core.common.di.qualifiers.Unauthenticated
import net.readian.parcel.data.api.CarriersApiService
import net.readian.parcel.data.api.ParcelApiService
import net.readian.parcel.data.network.ApiKeyInterceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {

  private const val TIMEOUT = 30L

  @Provides
  @Singleton
  @Unauthenticated
  fun unauthenticatedOkHttpClient(
    @ApplicationContext context: Context,
  ): OkHttpClient = OkHttpClient.Builder()
    .retryOnConnectionFailure(true)
    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
    .readTimeout(TIMEOUT, TimeUnit.SECONDS)
    .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
    .callTimeout(TIMEOUT, TimeUnit.SECONDS)
    .addInterceptor(ChuckerInterceptor.Builder(context).build())
    .build()

  @Provides
  @Singleton
  @Authenticated
  fun authenticatedOkHttpClient(
    @ApplicationContext context: Context,
    apiKeyInterceptor: ApiKeyInterceptor,
  ): OkHttpClient = OkHttpClient.Builder()
    .retryOnConnectionFailure(true)
    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
    .readTimeout(TIMEOUT, TimeUnit.SECONDS)
    .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
    .callTimeout(TIMEOUT, TimeUnit.SECONDS)
    .addInterceptor(apiKeyInterceptor)
    .addInterceptor(ChuckerInterceptor.Builder(context).build())
    .build()

  @Provides
  @Singleton
  @Authenticated
  fun authenticatedRetrofit(
    @ApiUrl baseUrl: String,
    @Authenticated client: OkHttpClient,
    converter: Converter.Factory,
  ): Retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(client)
    .addConverterFactory(converter)
    .build()

  @Provides
  @Singleton
  @Unauthenticated
  fun unauthenticatedRetrofit(
    @ApiUrl baseUrl: String,
    @Unauthenticated client: OkHttpClient,
    converter: Converter.Factory,
  ): Retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(client)
    .addConverterFactory(converter)
    .build()

  @Provides
  @Singleton
  @ApiUrl
  fun provideParcelApiUrl(): String = BuildConfig.API_BASE_URL

  @Provides
  @Singleton
  fun provideConverterFactory(json: Json): Converter.Factory =
    json.asConverterFactory("application/json".toMediaType())

  @Provides
  @Singleton
  fun provideParcelApiService(
    @Authenticated retrofit: Retrofit,
  ): ParcelApiService = retrofit.create(ParcelApiService::class.java)

  @Provides
  @Singleton
  fun provideCarriersApiService(
    @Unauthenticated retrofit: Retrofit,
  ): CarriersApiService = retrofit.create(CarriersApiService::class.java)
}
