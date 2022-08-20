package me.cniekirk.flex.ui.viewmodel

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import me.cniekirk.flex.MainCoroutineRule
import me.cniekirk.flex.R
import me.cniekirk.flex.data.Cause
import me.cniekirk.flex.data.remote.model.reddit.Comment
import me.cniekirk.flex.data.remote.model.reddit.CommentData
import me.cniekirk.flex.data.remote.model.reddit.MoreComments
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.CommentRequest
import me.cniekirk.flex.domain.usecase.*
import me.cniekirk.flex.ui.submission.state.SubmissionDetailEffect
import me.cniekirk.flex.ui.submission.state.SubmissionDetailState
import me.cniekirk.flex.ui.submission.state.VoteState
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.orbitmvi.orbit.test

@ExperimentalCoroutinesApi
class SubmissionDetailViewModelTest {

    private lateinit var sut: SubmissionDetailViewModel
    private val getCommentsUseCase = mockk<GetCommentsUseCase>()
    private val getMoreCommentsUseCase = mockk<GetMoreCommentsUseCase>()
    private val upvoteThingUseCase = mockk<UpvoteThingUseCase>()
    private val removeVoteThingUseCase = mockk<RemoveVoteThingUseCase>()
    private val downvoteThingUseCase = mockk<DownvoteThingUseCase>()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        sut = SubmissionDetailViewModel(
            getCommentsUseCase,
            getMoreCommentsUseCase,
            upvoteThingUseCase,
            removeVoteThingUseCase,
            downvoteThingUseCase
        )
    }

    @Test
    fun `getComments reduces state with comments when successful`() = runTest {
        // Arrange
        val comments = listOf(comment)
        coEvery { getCommentsUseCase.invoke(any()) } returns(flowOf(RedditResult.Success(comments)))
        val underTest = sut.test(SubmissionDetailState())

        // Act
        underTest.testIntent { getComments("t3_adsaj", "top") }

        // Assert
        underTest.assert(SubmissionDetailState()) {
            states(
                { copy(comments = comments) }
            )
        }
    }

    @Test
    fun `getComments posts ShowError effect with correct message when error is Unknown`() = runTest {
        // Arrange
        coEvery { getCommentsUseCase.invoke(any()) } returns(flowOf(RedditResult.Error(Cause.Unknown)))
        val underTest = sut.test(SubmissionDetailState())

        // Act
        underTest.testIntent { getComments("t3_adsaj", "top") }

        // Assert
        underTest.assert(SubmissionDetailState()) {
            postedSideEffects(
                SubmissionDetailEffect.ShowError(R.string.unknown_error)
            )
        }
    }

    @Test
    fun `getComments posts ShowError effect with correct message when error is NetworkError`() = runTest {
        // Arrange
        coEvery { getCommentsUseCase.invoke(any()) } returns(flowOf(RedditResult.Error(Cause.NetworkError)))
        val underTest = sut.test(SubmissionDetailState())

        // Act
        underTest.testIntent { getComments("t3_adsaj", "top") }

        // Assert
        underTest.assert(SubmissionDetailState()) {
            postedSideEffects(
                SubmissionDetailEffect.ShowError(R.string.generic_network_error)
            )
        }
    }

    @Test
    fun `getComments posts ShowError effect with correct message when error is Unauthenticated`() = runTest {
        // Arrange
        coEvery { getCommentsUseCase.invoke(any()) } returns(flowOf(RedditResult.Error(Cause.Unauthenticated)))
        val underTest = sut.test(SubmissionDetailState())

        // Act
        underTest.testIntent { getComments("t3_adsaj", "top") }

        // Assert
        underTest.assert(SubmissionDetailState()) {
            postedSideEffects(
                SubmissionDetailEffect.ShowError(R.string.action_error_aunauthenticated)
            )
        }
    }

    @Test
    fun `getMoreComments reduces state with new comments when successful`() = runTest {
        // Arrange
        val initialState = listOf(moreComments)
        val newComments = listOf(comment)
        coEvery { getMoreCommentsUseCase.invoke(any()) } returns flowOf(RedditResult.Success(newComments))
        val underTest = sut.test(SubmissionDetailState(comments = initialState))

        // Act
        underTest.testIntent { getMoreComments(moreComments, "t3_fsfdf") }

        // Assert
        underTest.assert(SubmissionDetailState(comments = initialState)) {
            states(
                { copy(comments = newComments) }
            )
        }
    }

    @Test
    fun `upvoteClicked posts UpdateVoteState effect with UpVote then NoVote states when invoked sequentially`() = runTest {
        // Arrange
        coEvery { upvoteThingUseCase.invoke(any()) } returns(flowOf(RedditResult.Success(true)))
        coEvery { removeVoteThingUseCase.invoke(any()) } returns(flowOf(RedditResult.Success(true)))
        val underTest = sut.test(SubmissionDetailState())

        // Act
        underTest.testIntent { upvoteClicked("t3_adsaj") }
        underTest.testIntent { upvoteClicked("t3_adsaj") }

        // Assert
        underTest.assert(SubmissionDetailState()) {
            postedSideEffects(
                SubmissionDetailEffect.UpdateVoteState(VoteState.Upvote),
                SubmissionDetailEffect.UpdateVoteState(VoteState.NoVote)
            )
        }
    }

    @Test
    fun `downvoteClicked posts UpdateVoteState effect with DownVote then NoVote states when invoked sequentially`() = runTest {
        // Arrange
        coEvery { downvoteThingUseCase.invoke(any()) } returns(flowOf(RedditResult.Success(true)))
        coEvery { removeVoteThingUseCase.invoke(any()) } returns(flowOf(RedditResult.Success(true)))
        val underTest = sut.test(SubmissionDetailState())

        // Act
        underTest.testIntent { downvoteClicked("t3_adsaj") }
        underTest.testIntent { downvoteClicked("t3_adsaj") }

        // Assert
        underTest.assert(SubmissionDetailState()) {
            postedSideEffects(
                SubmissionDetailEffect.UpdateVoteState(VoteState.Downvote),
                SubmissionDetailEffect.UpdateVoteState(VoteState.NoVote)
            )
        }
    }

    @Test
    fun `downvoteClicked then upvoteClicked posts UpdateVoteState effect with DownVote then UpVote states`() = runTest {
        // Arrange
        coEvery { downvoteThingUseCase.invoke(any()) } returns(flowOf(RedditResult.Success(true)))
        coEvery { upvoteThingUseCase.invoke(any()) } returns(flowOf(RedditResult.Success(true)))
        val underTest = sut.test(SubmissionDetailState())

        // Act
        underTest.testIntent { downvoteClicked("t3_adsaj") }
        underTest.testIntent { upvoteClicked("t3_adsaj") }

        // Assert
        underTest.assert(SubmissionDetailState()) {
            postedSideEffects(
                SubmissionDetailEffect.UpdateVoteState(VoteState.Downvote),
                SubmissionDetailEffect.UpdateVoteState(VoteState.Upvote)
            )
        }
    }

    @Test
    fun `upvoteClicked then downvoteClicked posts UpdateVoteState effect with UpVote then DownVote states`() = runTest {
        // Arrange
        coEvery { downvoteThingUseCase.invoke(any()) } returns(flowOf(RedditResult.Success(true)))
        coEvery { upvoteThingUseCase.invoke(any()) } returns(flowOf(RedditResult.Success(true)))
        val underTest = sut.test(SubmissionDetailState())

        // Act
        underTest.testIntent { upvoteClicked("t3_adsaj") }
        underTest.testIntent { downvoteClicked("t3_adsaj") }

        // Assert
        underTest.assert(SubmissionDetailState()) {
            postedSideEffects(
                SubmissionDetailEffect.UpdateVoteState(VoteState.Upvote),
                SubmissionDetailEffect.UpdateVoteState(VoteState.Downvote)
            )
        }
    }

    companion object {
        val comment = Comment(
            "123",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null
        )
        val moreComments = MoreComments(
            "123",
            "",
            0,
            0,
            "",
            listOf()
        )
    }
}