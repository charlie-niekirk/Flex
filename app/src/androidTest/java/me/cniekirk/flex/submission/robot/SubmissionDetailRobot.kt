package me.cniekirk.flex.submission.robot

import me.cniekirk.flex.R

fun submissionDetail(func: SubmissionDetailRobot.() -> Unit) = SubmissionDetailRobot().apply(func)

class SubmissionDetailRobot : BaseTestRobot() {

    fun verifyUpvoteVisible() {
        checkIfViewDisplayed(R.id.button_upvote_action)
    }

    fun verifyDownvoteVisible() {
        checkIfViewDisplayed(R.id.button_downvote_action)
    }

    fun verifyReplyVisible() {
        checkIfViewDisplayed(R.id.button_reply_action)
    }

    fun verifyOptionsVisible() {
        checkIfViewDisplayed(R.id.button_options_action)
    }

    fun verifyTitleDisplayed(title: String) {
        checkIfTextDisplayed(title)
    }
}