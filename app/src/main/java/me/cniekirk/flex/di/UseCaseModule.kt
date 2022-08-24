package me.cniekirk.flex.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.cniekirk.flex.domain.usecase.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Provides
    @Singleton
    fun provideGetSelfPostsUseCase(getSelfPostsUseCaseImpl: GetSelfPostsUseCaseImpl): GetSelfPostsUseCase
        = getSelfPostsUseCaseImpl

    @Provides
    @Singleton
    fun provideThingInfoUseCase(getThingInfoUseCaseImpl: GetThingInfoUseCaseImpl): GetThingInfoUseCase
        = getThingInfoUseCaseImpl

    @Provides
    @Singleton
    fun provideGetCommentsUseCase(getCommentsUseCaseImpl: GetCommentsUseCaseImpl): GetCommentsUseCase
        = getCommentsUseCaseImpl

    @Provides
    @Singleton
    fun provideGetMoreCommentsUseCase(getMoreCommentsUseCaseImpl: GetMoreCommentsUseCaseImpl): GetMoreCommentsUseCase
        = getMoreCommentsUseCaseImpl

    @Provides
    @Singleton
    fun provideUpvoteThingUseCase(upvoteThingUseCaseImpl: UpvoteThingUseCaseImpl): UpvoteThingUseCase
        = upvoteThingUseCaseImpl

    @Provides
    @Singleton
    fun provideDownvoteThingUseCase(downvoteThingUseCaseImpl: DownvoteThingUseCaseImpl): DownvoteThingUseCase
        = downvoteThingUseCaseImpl

    @Provides
    @Singleton
    fun provideRemoveVoteThingUseCase(removeVoteThingUseCaseImpl: RemoveVoteThingUseCaseImpl): RemoveVoteThingUseCase
        = removeVoteThingUseCaseImpl
}