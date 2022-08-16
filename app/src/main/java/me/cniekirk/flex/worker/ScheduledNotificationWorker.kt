package me.cniekirk.flex.worker

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.cniekirk.flex.FlexApp.Companion.CHANNEL_ID
import me.cniekirk.flex.util.isAllowedByUser
import java.security.SecureRandom

@HiltWorker
class ScheduledNotificationWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParameters: WorkerParameters
) : Worker(appContext, workerParameters) {

    override fun doWork(): Result {
        val thingId = inputData.getString("THING_ID") ?: return Result.failure()
        return if (postNotification(thingId)) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun postNotification(thingId: String): Boolean {
        return if (isAllowedByUser(appContext, POST_NOTIFICATIONS)) {
            val builder = NotificationCompat.Builder(appContext, CHANNEL_ID)
                .setContentTitle("Reminder: some text here")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            with(NotificationManagerCompat.from(appContext)) {
                notify(SecureRandom().nextInt(), builder.build())
            }
            true
        } else {
            false
        }
    }
}