package net.readian.parcel.data.api

import retrofit2.http.GET

interface CarriersApiService {
    @GET("external/supported_carriers.json")
    suspend fun getSupportedCarriers(): Map<String, String>
}
