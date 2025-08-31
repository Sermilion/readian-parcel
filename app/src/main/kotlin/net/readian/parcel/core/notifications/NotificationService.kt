package net.readian.parcel.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import net.readian.parcel.R
import net.readian.parcel.core.notifications.model.NotificationData
import net.readian.parcel.main.ui.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService @Inject constructor(
  @ApplicationContext private val context: Context,
) {
  private val notificationManager =
    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  init {
    createNotificationChannel()
  }

  private fun createNotificationChannel() {
    val channel = NotificationChannel(
      CHANNEL_ID,
      "Package Status Updates",
      NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
      description = "Notifications for package status changes"
      enableLights(true)
      enableVibration(true)
    }
    notificationManager.createNotificationChannel(channel)
  }

  fun showPackageStatusUpdate(update: NotificationData) {
    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      update.trackingNumber.hashCode(),
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentTitle("Package Status Update")
      .setContentText("${update.description} is now ${update.newStatus}")
      .setStyle(
        NotificationCompat.BigTextStyle()
          .bigText(
            "${update.description} (${update.trackingNumber})\n" +
              "Status changed from ${update.oldStatus} to ${update.newStatus}\n" +
              "Carrier: ${update.carrierName ?: update.carrierCode}",
          ),
      )
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .build()

    notificationManager.notify(
      update.trackingNumber.hashCode(),
      notification,
    )
  }

  companion object {
    private const val CHANNEL_ID = "package_status_updates"
  }
}
