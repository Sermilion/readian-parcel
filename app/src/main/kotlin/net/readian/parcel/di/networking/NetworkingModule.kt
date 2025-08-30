package net.readian.parcel.di.networking

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.readian.parcel.di.qualifiers.ApiUrl
import net.readian.parcel.di.qualifiers.Authenticated
import net.readian.parcel.di.qualifiers.Unauthenticated
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import net.readian.parcel.data.api.ParcelApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {

    @Provides
    @Singleton
    @Unauthenticated
    fun unauthenticatedOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(ChuckerInterceptor.Builder(context).build())
        .build()
        
    @Provides
    @Singleton
    @Authenticated
    fun authenticatedOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient = OkHttpClient.Builder()
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
    fun provideParcelApiUrl(): String = "https://api.parcel.app/"

    @Provides
    @Singleton
    fun provideConverterFactory(): Converter.Factory = 
        Json.asConverterFactory("application/json".toMediaType())

    @Provides
    @Singleton
    fun provideParcelApiService(
        @Unauthenticated retrofit: Retrofit
    ): ParcelApiService = retrofit.create(ParcelApiService::class.java)
}
