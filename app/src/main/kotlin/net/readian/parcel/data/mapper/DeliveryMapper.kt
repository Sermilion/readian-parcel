package net.readian.parcel.data.mapper

import net.readian.parcel.data.model.DeliveryEventResponse
import net.readian.parcel.data.model.DeliveryResponse
import net.readian.parcel.data.model.DeliveryStatusResponse
import net.readian.parcel.data.model.RateLimitInfoDataModel
import net.readian.parcel.domain.model.Delivery
import net.readian.parcel.domain.model.DeliveryEvent
import net.readian.parcel.domain.model.DeliveryStatus
import net.readian.parcel.domain.model.RateLimitInfo
import timber.log.Timber
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Maps data layer models to domain layer models
 * This enforces the dependency rule: data layer depends on domain, not vice versa
 */
object DeliveryMapper {

  fun toDomain(dataModel: DeliveryResponse): Delivery = Delivery(
    trackingNumber = dataModel.trackingNumber,
    carrierCode = dataModel.carrierCode,
    description = dataModel.description,
    status = dataModel.statusCode.toDeliveryStatus(),
    events = dataModel.events.map { toDomain(it) },
    extraInformation = dataModel.extraInformation,
    expectedAt = dataModel.timestampExpected
      ?: dataModel.dateExpected?.let { parseDateToTimestampOrNull(it) },
    expectedEndAt = dataModel.timestampExpectedEnd
      ?: dataModel.dateExpectedEnd?.let { parseDateToTimestampOrNull(it) },
    expectedDateRaw = dataModel.dateExpected,
    expectedEndDateRaw = dataModel.dateExpectedEnd,
  )

  fun toDomain(dataModel: DeliveryEventResponse): DeliveryEvent = DeliveryEvent(
    timestamp = parseDateToTimestampOrNull(dataModel.date),
    description = dataModel.event,
    location = dataModel.location,
    rawDate = dataModel.date,
  )

  @Suppress("TooGenericExceptionCaught")
  fun parseDateToTimestampOrNull(dateString: String): Long? {
    return runCatching {
      Instant.parse(dateString).toEpochMilli()
    }.recoverCatching {
      OffsetDateTime.parse(dateString).toInstant().toEpochMilli()
    }.recoverCatching {
      val formatters = listOf(
        DateTimeFormatter.ISO_ZONED_DATE_TIME,
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ofPattern(TIME_FORMAT),
        DateTimeFormatter.ofPattern(TIME_FORMAT),
      )
      for (f in formatters) {
        try {
          val ldt = java.time.LocalDateTime.parse(dateString, f)
          return@recoverCatching ldt.atZone(java.time.ZoneOffset.UTC).toInstant().toEpochMilli()
        } catch (e: Exception) {
          Timber.v(e, "Date format %s did not match: %s", f, dateString)
        }
      }
      // Epoch millis as string
      dateString.toLongOrNull()
    }.getOrNull()
  }

  fun toDomain(dataModel: RateLimitInfoDataModel): RateLimitInfo = RateLimitInfo(
    remainingRequests = dataModel.remainingRequests,
    timeUntilNextRequestMs = dataModel.timeUntilNextRequestMs,
  )

  private fun Int.toDeliveryStatus(): DeliveryStatus {
    val dataStatus = DeliveryStatusResponse.fromCode(this)
    return when (dataStatus) {
      DeliveryStatusResponse.COMPLETED -> DeliveryStatus.COMPLETED
      DeliveryStatusResponse.FROZEN -> DeliveryStatus.FROZEN
      DeliveryStatusResponse.IN_TRANSIT -> DeliveryStatus.IN_TRANSIT
      DeliveryStatusResponse.EXPECTING_PICKUP -> DeliveryStatus.EXPECTING_PICKUP
      DeliveryStatusResponse.OUT_FOR_DELIVERY -> DeliveryStatus.OUT_FOR_DELIVERY
      DeliveryStatusResponse.NOT_FOUND -> DeliveryStatus.NOT_FOUND
      DeliveryStatusResponse.FAILED_DELIVERY -> DeliveryStatus.FAILED_DELIVERY
      DeliveryStatusResponse.EXCEPTION -> DeliveryStatus.EXCEPTION
      DeliveryStatusResponse.CARRIER_INFORMED -> DeliveryStatus.CARRIER_INFORMED
    }
  }

  private const val TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
}
