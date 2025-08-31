package net.readian.parcel.domain.repository

import kotlinx.coroutines.flow.Flow

interface CarrierRepository {
  /** Flow of carrier code -> human-friendly name */
  val carriers: Flow<Map<String, String>>

  /** Refresh carriers from network and cache them. */
  suspend fun refresh()

  /** Get single carrier by code. */
  suspend fun getCarrier(code: String): net.readian.parcel.data.database.entity.CarrierDataModel?
}
