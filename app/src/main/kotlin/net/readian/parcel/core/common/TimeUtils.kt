package net.readian.parcel.core.common

import android.text.format.DateUtils
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TimeUtils {
  fun formatTimestamp(epochMillis: Long): String {
    val instant = Instant.ofEpochMilli(epochMillis)
    val zdt = instant.atZone(ZoneId.systemDefault())
    return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
  }

  fun formatRelativeTime(epochMillis: Long): String = DateUtils.getRelativeTimeSpanString(
    epochMillis,
    System.currentTimeMillis(),
    DateUtils.MINUTE_IN_MILLIS,
    DateUtils.FORMAT_ABBREV_RELATIVE,
  ).toString()

  fun formattedAbsoluteAndRelative(epochMillis: Long): String {
    val absolute = formatTimestamp(epochMillis)
    val relative = formatRelativeTime(epochMillis)
    return if (relative.isNotBlank()) "$absolute ($relative)" else absolute
  }
}
