package me.cniekirk.flex.domain

import androidx.work.OneTimeWorkRequest

interface WorkerRepository {

    fun scheduleOneTimeWork(oneTimeWorkRequest: OneTimeWorkRequest)
}