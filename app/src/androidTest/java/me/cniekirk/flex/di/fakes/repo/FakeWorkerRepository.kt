package me.cniekirk.flex.di.fakes.repo

import androidx.work.OneTimeWorkRequest
import me.cniekirk.flex.domain.WorkerRepository
import javax.inject.Inject

class FakeWorkerRepository @Inject constructor() : WorkerRepository {

    override fun scheduleOneTimeWork(oneTimeWorkRequest: OneTimeWorkRequest) {

    }
}
