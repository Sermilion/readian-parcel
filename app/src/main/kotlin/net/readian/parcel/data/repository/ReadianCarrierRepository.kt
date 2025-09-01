package net.readian.parcel.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import net.readian.parcel.core.common.DispatcherProvider
import net.readian.parcel.data.api.CarriersApiService
import net.readian.parcel.data.database.dao.CarrierDao
import net.readian.parcel.data.database.entity.CarrierDataModel
import net.readian.parcel.domain.model.Carrier
import net.readian.parcel.domain.repository.CarrierRepository
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadianCarrierRepository @Inject constructor(
  private val api: CarriersApiService,
  private val carrierDao: CarrierDao,
  private val dispatcherProvider: DispatcherProvider,
) : CarrierRepository {

  override val carriers: Flow<Map<String, String>> = carrierDao
    .observeAll()
    .map { list -> list.associate { it.code to it.name } }
    .distinctUntilChanged()

  override suspend fun refresh() = withContext(dispatcherProvider.io()) {
    try {
      val map = api.getSupportedCarriers()
      val now = System.currentTimeMillis()
      val entities = map.entries.map { (code, name) ->
        CarrierDataModel(code = code, name = name, updatedAt = now)
      }
      carrierDao.insertAll(entities)
    } catch (e: IOException) {
      Timber.e(e, "Network error refreshing carriers.")
    } catch (e: HttpException) {
      Timber.e(e, "HTTP error refreshing carriers.")
    }
  }

  override suspend fun getCarrier(code: String): Carrier? = withContext(dispatcherProvider.io()) {
    carrierDao.getByCode(code)?.toDomain()
  }
}

private fun CarrierDataModel.toDomain(): Carrier = Carrier(code = code, name = name)
