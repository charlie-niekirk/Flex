package me.cniekirk.flex.submission

import android.content.Context
import androidx.core.os.bundleOf
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.data.remote.model.reddit.Gildings
import me.cniekirk.flex.data.remote.model.reddit.MediaEmbed
import me.cniekirk.flex.data.remote.model.reddit.SecureMediaEmbed
import me.cniekirk.flex.submission.robot.submissionDetail
import me.cniekirk.flex.ui.submission.SubmissionDetailFragment
import me.cniekirk.flex.ui.submission.state.VoteState
import me.cniekirk.flex.util.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SubmissionDetailFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testAllActionsAreVisible() {
        launchFragmentInHiltContainer<SubmissionDetailFragment>(fragmentDetailArgs)
        submissionDetail {
            verifyUpvoteVisible()
            verifyDownvoteVisible()
            verifyReplyVisible()
            verifyOptionsVisible()
        }
    }

    @Test
    fun testTitleIsDisplayedCorrectly() {
        launchFragmentInHiltContainer<SubmissionDetailFragment>(fragmentDetailArgs)
        val numComments = fragmentDetailArgs.getParcelable("post", AuthedSubmission::class.java)?.numComments
        val expectedTitle = ApplicationProvider.getApplicationContext<Context>().getString(R.string.comments_title, numComments)
        submissionDetail {
            verifyTitleDisplayed(expectedTitle)
        }
    }

//    @Test
//    fun

    companion object {
        val fragmentDetailArgs = bundleOf(
            "post" to AuthedSubmission(
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
                null,
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
                null,
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
        )
    }
}