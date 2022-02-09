package me.cniekirk.flex.data

import me.cniekirk.flex.data.remote.model.reddit.Comment
import me.cniekirk.flex.data.remote.model.reddit.CommentData
import me.cniekirk.flex.data.remote.model.reddit.Gildings
import me.cniekirk.flex.data.remote.model.reddit.base.EnvelopeKind
import me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedComment
import me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedCommentData
import me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedCommentDataListing
import me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedContributionListing
import me.cniekirk.flex.data.remote.model.reddit.listings.CommentDataListing
import me.cniekirk.flex.data.remote.model.reddit.listings.ContributionListing

private val any = Any()
private val gildings = Gildings(1, 2)

fun provideComment(depth: Int = 0): Comment {
    return Comment(
        "1",
        "hello",
        null,
        "charlie",
        "comment",
        "comment",
        false,
        123456L,
        123456L,
        any,
        depth,
        null,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        null,
        null,
        null,
        null,
        null,
        gildings,
        "t3_jfdsj",
        "https://i.redd.it/jfishgfudihfguids.jpg",
        null,
        null,
        "t3_hfdusk",
        123,
        "tommyinnit",
        "t2_fhhdjks",
        "r/tommyinnit"
    )
}

private val commentData by lazy(LazyThreadSafetyMode.NONE) {
    val reply = provideComment(depth = 1)
    val repliesRaw = EnvelopedCommentDataListing(EnvelopeKind.Listing, CommentDataListing("", 1, listOf(
        EnvelopedCommentData(EnvelopeKind.Comment, reply)
    ), "", ""))
    provideComment().copy(replies = listOf(reply), repliesRaw = repliesRaw)
}

fun provideUnmappedComments(): List<EnvelopedContributionListing> {
    val comment = commentData

    val envelopedContributionListing = mutableListOf<EnvelopedContributionListing>().apply {
        add(EnvelopedContributionListing(
            EnvelopeKind.Listing,
            ContributionListing(
                "fhdsjkfhjkds",
                0,
                listOf(EnvelopedComment(EnvelopeKind.Comment, comment as Comment)),
                "dasda",
                "dsads")
            )
        )
    }
    return envelopedContributionListing
}

fun provideMappedComments(): List<CommentData> {
    val comments = mutableListOf<CommentData>()
    val comment = commentData
    comments.addAll(listOf(comment, comment.replies?.get(0)!!))
    return comments
}