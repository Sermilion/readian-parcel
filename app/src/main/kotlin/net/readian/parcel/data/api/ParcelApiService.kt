package net.readian.parcel.data.api

import net.readian.parcel.data.model.ParcelResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ParcelApiService {
    
    @GET("external/deliveries/")
    suspend fun getDeliveries(
        @Header("api-key") apiKey: String,
        @Query("filter_mode") filterMode: String = "recent"
    ): Response<ParcelResponse>
}