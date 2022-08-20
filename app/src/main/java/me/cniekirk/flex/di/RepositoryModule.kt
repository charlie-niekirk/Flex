package me.cniekirk.flex.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.cniekirk.flex.data.local.repo.LocalDataRepositoryImpl
import me.cniekirk.flex.data.local.repo.WorkerRepositoryImpl
import me.cniekirk.flex.data.remote.repo.FlexDataRepositoryImpl
import me.cniekirk.flex.data.remote.repo.ImgurDataRepositoryImpl
import me.cniekirk.flex.data.remote.repo.RedditDataRepositoryImpl
import me.cniekirk.flex.domain.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideRedditDataRepo(redditDataRepositoryImpl: RedditDataRepositoryImpl)
            : RedditDataRepository = redditDataRepositoryImpl

    @Provides
    @Singleton
    fun provideLocalDataRepo(localDataRepositoryImpl: LocalDataRepositoryImpl)
            : LocalDataRepository = localDataRepositoryImpl

    @Provides
    @Singleton
    fun provideImgurDataRepo(imgurDataRepositoryImpl: ImgurDataRepositoryImpl)
            : ImgurDataRepository = imgurDataRepositoryImpl

    @Provides
    @Singleton
    fun provideFlexDataRepo(flexDataRepositoryImpl: FlexDataRepositoryImpl)
            : FlexDataRepository = flexDataRepositoryImpl

    @Provides
    @Singleton
    fun provideWorkerRepo(workerRepositoryImpl: WorkerRepositoryImpl)
            : WorkerRepository = workerRepositoryImpl
}