package me.cniekirk.flex

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import me.cniekirk.flex.di.UseCaseModule
import me.cniekirk.flex.di.fakes.usecase.*
import me.cniekirk.flex.domain.usecase.*
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [UseCaseModule::class]
)
class FakeUseCaseModule {

    @Provides
    @Singleton
    fun provideGetSelfPostsUseCase(fakeGetSelfPostsUseCase: FakeGetSelfPostsUseCase): GetSelfPostsUseCase
            = fakeGetSelfPostsUseCase

    @Provides
    @Singleton
    fun provideThingInfoUseCase(fakeGetThingInfoUseCase: FakeGetThingInfoUseCase): GetThingInfoUseCase
            = fakeGetThingInfoUseCase

    @Provides
    @Singleton
    fun provideGetCommentsUseCase(fakeGetCommentsUseCase: FakeGetCommentsUseCase): GetCommentsUseCase
            = fakeGetCommentsUseCase

    @Provides
    @Singleton
    fun provideGetMoreCommentsUseCase(fakeGetMoreCommentsUseCase: FakeGetMoreCommentsUseCase): GetMoreCommentsUseCase
            = fakeGetMoreCommentsUseCase

    @Provides
    @Singleton
    fun provideUpvoteThingUseCase(fakeUpvoteThingUseCase: FakeUpvoteThingUseCase): UpvoteThingUseCase
            = fakeUpvoteThingUseCase

    @Provides
    @Singleton
    fun provideDownvoteThingUseCase(fakeDownvoteThingUseCase: FakeDownvoteThingUseCase): DownvoteThingUseCase
            = fakeDownvoteThingUseCase

    @Provides
    @Singleton
    fun provideRemoveVoteThingUseCase(fakeRemoveVoteThingUseCase: FakeRemoveVoteThingUseCase): RemoveVoteThingUseCase
            = fakeRemoveVoteThingUseCase
}