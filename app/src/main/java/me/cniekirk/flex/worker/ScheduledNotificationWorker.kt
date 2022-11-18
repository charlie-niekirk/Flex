package me.cniekirk.flex.worker

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.cniekirk.flex.FlexApp.Companion.CHANNEL_ID
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.usecase.GetThingInfoUseCase
import me.cniekirk.flex.ui.activity.ContainerActivity
import me.cniekirk.flex.ui.submission.model.toUiSubmission
import me.cniekirk.flex.util.isAllowedByUser
import java.security.SecureRandom

@HiltWorker
class ScheduledNotificationWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val getThingInfoUseCase: GetThingInfoUseCase
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val thingId = inputData.getString("THING_ID") ?: return Result.failure()
        process(thingId)
        return Result.success()
    }

    private suspend fun process(thingId: String) {
        getThingInfoUseCase(thingId).
                collect { response ->
                    when (response) {
                        is RedditResult.Error -> {

                        }
                        RedditResult.Loading -> {}
                        is RedditResult.Success -> {
                            postNotification(response.data)
                        }
                    }
                }
    }

    private fun postNotification(post: AuthedSubmission): Boolean {
        return if (isAllowedByUser(appContext, POST_NOTIFICATIONS)) {
            val uiSubmission = post.toUiSubmission()

            val intent = Intent(appContext, ContainerActivity::class.java).apply {
                putExtra("submission", uiSubmission)
            }
            val pending = PendingIntent.getActivity(appContext, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)

            val builder = NotificationCompat.Builder(appContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.lightbulb)
                .setContentTitle("Reminder: ${post.title}")
                .setContentText("by ${post.authorFullname}")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pending)
            with(NotificationManagerCompat.from(appContext)) {
                notify(SecureRandom().nextInt(), builder.build())
            }
            true
        } else {
            false
        }
    }
}