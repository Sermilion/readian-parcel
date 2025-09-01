package net.readian.parcel.domain.repository

import kotlinx.coroutines.flow.Flow
import net.readian.parcel.domain.model.Carrier

interface CarrierRepository {
  val carriers: Flow<Map<String, String>>
  suspend fun refresh()
  suspend fun getCarrier(code: String): Carrier?
}
