package me.cniekirk.flex.data.local.repo

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import me.cniekirk.flex.domain.WorkerRepository
import javax.inject.Inject

class WorkerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : WorkerRepository {

    override fun scheduleOneTimeWork(oneTimeWorkRequest: OneTimeWorkRequest) {
        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
    }
}