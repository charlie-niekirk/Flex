package me.cniekirk.flex.submission

import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.paging.PagingData
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.data.remote.model.reddit.Gildings
import me.cniekirk.flex.data.remote.model.reddit.MediaEmbed
import me.cniekirk.flex.data.remote.model.reddit.SecureMediaEmbed
import me.cniekirk.flex.ui.activity.ContainerActivity
import me.cniekirk.flex.ui.compose.styles.FlexTheme
import me.cniekirk.flex.ui.submission.SubmissionListContent
import me.cniekirk.flex.ui.submission.state.SubmissionListState
import me.cniekirk.flex.ui.submission.state.VoteState
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@ExperimentalCoroutinesApi
class SubmissionListFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var composeTestRule = createAndroidComposeRule<ContainerActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        val items = flowOf(PagingData.from(createFakeData()))
        val state = mutableStateOf(SubmissionListState(items, SUBREDDIT, SORT))
        composeTestRule.activity.setContent {
            FlexTheme {
                SubmissionListContent(state) {}
            }
        }
    }

    @Test
    fun subreddit_name_isDisplayed() {
        composeTestRule.onNodeWithTag("subreddit").assertIsDisplayed()
        composeTestRule.onNodeWithTag("subreddit").assertTextEquals(SUBREDDIT)
    }

    @Test
    fun correct_number_of_submissions_displayed() {
        composeTestRule.onNodeWithTag("submission_list").onChildren().assertCountEquals(NUM_POSTS)
    }

    private fun createFakeData(): List<AuthedSubmission> {
        val post = AuthedSubmission(
            null,
            false,
            null,
            null,
            false,
            "chertycherty",
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
            false,
            null,
            false,
            null,
            false,
            null,
            1111111.0,
            null,
            null,
            null,
            "",
            0,
            null,
            0,
            Gildings(null, null),
            null,
            false,
            false,
            "",
            false,
            true,
            false,
            true,
            false,
            false,
            true,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            MediaEmbed(null, null, null, null),
            null,
            false,
            null,
            null,
            null,
            null,
            "",
            false,
            10,
            0,
            null,
            false,
            null,
            "",
            false,
            null,
            null,
            null,
            null,
            false,
            null,
            null,
            null,
            null,
            false,
            50,
            null,
            SecureMediaEmbed(null, null, null, null, null),
            "This is a test post with text",
            null,
            false,
            false,
            null,
            "flexforreddit",
            "",
            "r/flexforreddit",
            0,
            "",
            "",
            "",
            null,
            null,
            "Test post title",
            null,
            0,
            null,
            null,
            0.9,
            "https://www.bbc.co.uk",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            VoteState.NoVote
        )
        return listOf(post, post, post, post, post)
    }

    companion object {
        const val SUBREDDIT = "flexapp"
        const val SORT = "new"
        const val NUM_POSTS = 5
    }
}