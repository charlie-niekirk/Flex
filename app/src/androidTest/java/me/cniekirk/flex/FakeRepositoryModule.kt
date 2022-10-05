package me.cniekirk.flex

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import me.cniekirk.flex.di.RepositoryModule
import me.cniekirk.flex.di.fakes.repo.FakeImgurDataRepository
import me.cniekirk.flex.di.fakes.repo.FakeLocalDataRepository
import me.cniekirk.flex.di.fakes.repo.FakeRedditDataRepository
import me.cniekirk.flex.di.fakes.repo.FakeWorkerRepository
import me.cniekirk.flex.domain.ImgurDataRepository
import me.cniekirk.flex.domain.LocalDataRepository
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.WorkerRepository
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
class FakeRepositoryModule {

    @Provides
    @Singleton
    fun provideRedditDataRepo(fakeRedditDataRepository: FakeRedditDataRepository)
            : RedditDataRepository = fakeRedditDataRepository

    @Provides
    @Singleton
    fun provideLocalDataRepo(fakeLocalDataRepository: FakeLocalDataRepository)
            : LocalDataRepository = fakeLocalDataRepository

    @Provides
    @Singleton
    fun provideImgurDataRepo(fakeImgurDataRepository: FakeImgurDataRepository)
            : ImgurDataRepository = fakeImgurDataRepository

    @Provides
    @Singleton
    fun provideWorkerRepo(fakeWorkerRepository: FakeWorkerRepository)
            : WorkerRepository = fakeWorkerRepository
}