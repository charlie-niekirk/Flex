package me.cniekirk.flex

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.cniekirk.flex.ui.submission.SubmissionListFragment
import me.cniekirk.flex.util.launchFragmentInHiltContainer
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SubmissionListFragmentTest {

    @Test
    fun initialChecks() {
        val scenario = launchFragmentInHiltContainer<SubmissionListFragment> {

        }
    }

}