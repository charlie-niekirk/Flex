package me.cniekirk.flex

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import me.cniekirk.flex.data.provideMappedComments
import me.cniekirk.flex.data.provideUnmappedComments
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.CommentRequest
import me.cniekirk.flex.domain.usecase.GetCommentsUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import kotlin.time.ExperimentalTime

/**
 * Tests for getting comments from a submission and mapping the returned comment
 * tree appropriately
 */
@ExperimentalCoroutinesApi
class GetCommentsTest {

    private val coroutineDispatcher = TestCoroutineDispatcher()
    private lateinit var underTest: GetCommentsUseCase

    private val request = CommentRequest("123", "top")
    // Model class created by Moshi deserializing the JSON
    private val repositoryResponse = provideUnmappedComments()
    // The data structure the Use Case should emit for UI consumption
    private val useCaseResponse = provideMappedComments()

    private val repository = mock<RedditDataRepository> {
        onBlocking { getComments("123", "top") }
            .thenReturn(repositoryResponse)
    }

    @Before
    fun setup() {
        underTest = GetCommentsUseCase(repository, coroutineDispatcher)
    }

    @ExperimentalTime
    @Test
    fun `get comments mapped data and state emitted correctly`() = runTest {
        underTest(request).test {
            assertEquals(RedditResult.Loading, awaitItem())
            assertEquals(RedditResult.Success(useCaseResponse), awaitItem())
            awaitComplete()
        }
    }
}