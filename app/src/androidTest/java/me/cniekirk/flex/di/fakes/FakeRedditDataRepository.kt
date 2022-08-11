package me.cniekirk.flex.di.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.data.remote.model.pushshift.DeletedComment
import me.cniekirk.flex.data.remote.model.reddit.*
import me.cniekirk.flex.data.remote.model.reddit.auth.RedditUser
import me.cniekirk.flex.data.remote.model.reddit.auth.Token
import me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedContributionListing
import me.cniekirk.flex.data.remote.model.reddit.flair.UserFlairItem
import me.cniekirk.flex.data.remote.model.reddit.rules.Rules
import me.cniekirk.flex.data.remote.model.reddit.subreddit.ModUser
import me.cniekirk.flex.data.remote.model.reddit.subreddit.Subreddit
import me.cniekirk.flex.data.remote.model.wikipedia.WikiSummary
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.gallery.DownloadState

class FakeRedditDataRepository : RedditDataRepository {

    override suspend fun getComments(
        submissionId: String,
        sortType: String
    ): List<EnvelopedContributionListing> {
        return listOf(

        )
    }

    override fun getMoreComments(
        moreComments: MoreComments,
        parentId: String
    ): Flow<RedditResult<List<CommentData>>> {
        TODO("Not yet implemented")
    }

    override fun getDeletedComment(commentId: String): Flow<RedditResult<DeletedComment>> {
        return flowOf()
    }

    override fun getAccessToken(code: String): Flow<RedditResult<Token>> {
        return flowOf()
    }

    override fun upvoteThing(thingId: String): Flow<RedditResult<Boolean>> {
        return flowOf()
    }

    override fun removeVoteThing(thingId: String): Flow<RedditResult<Boolean>> {
        return flowOf()
    }

    override fun downvoteThing(thingId: String): Flow<RedditResult<Boolean>> {
        return flowOf()
    }

    override fun searchSubreddits(
        query: String,
        sortType: String
    ): Flow<RedditResult<List<Subreddit>>> {
        return flowOf()
    }

    override fun getSubredditRules(subreddit: String): Flow<RedditResult<Rules>> {
        return flowOf()
    }

    override fun getSubredditInfo(subreddit: String): Flow<RedditResult<Subreddit>> {
        return flowOf()
    }

    override fun getSubredditModerators(subreddit: String): Flow<RedditResult<List<ModUser>>> {
        return flowOf()
    }

    override fun subscribeSubreddit(subredditId: String): Flow<RedditResult<Int>> {
        return flowOf()
    }

    override fun unsubscribeSubreddit(subredditId: String): Flow<RedditResult<Int>> {
        return flowOf()
    }

    override fun favoriteSubreddit(subreddit: String): Flow<RedditResult<Int>> {
        return flowOf()
    }

    override fun unfavoriteSubreddit(subreddit: String): Flow<RedditResult<Int>> {
        return flowOf()
    }

    override fun getAvailableUserFlairs(subreddit: String): Flow<RedditResult<List<UserFlairItem>>> {
        return flowOf()
    }

    override fun submitComment(
        markdown: String,
        parentThing: String
    ): Flow<RedditResult<CommentData>> {
        return flowOf()
    }

    override fun downloadMedia(url: String): Flow<RedditResult<DownloadState>> {
        return flowOf()
    }

    override fun getMe(): Flow<RedditResult<RedditUser>> {
        return flowOf()
    }

    override fun getSelfPosts(username: String): Flow<PagingData<AuthedSubmission>> {
        return flowOf()
    }

    override suspend fun getWikipediaSummary(article: String): RedditResult<WikiSummary> {
        return RedditResult.Loading
    }

    companion object {
        val COMM = Comment(

        )
        const val COMMENT = listOf(
            Comment(id="i9pg80e", fullname="t1_i9pg80e", allAwarding= emptyList(), author="""2400xt, body=>	Added Video Deblurinator feature/setting which helps with videos not appearing blurry for the first few seconds

                Yes!!, bodyHtml=<div class="md"><blockquote>
        <p>Added Video Deblurinator feature/setting which helps with videos not appearing blurry for the first few seconds</p>
        </blockquote>

        <p>Yes!!</p>
        </div>, canGild=true, created=1653327020, createdUtc=1653327020, editedRaw=false, depth=0, distinguishedRaw=null, isArchived=false, isLocked=false, isSaved=false, isScoreHidden=false, isStickied=false, isSubmitter=false, likes=null, linkTitle=null, linkAuthor=null, linkId=t3_uw5c5r, linkUrl=null, linkPermalink=null, gildings=Gildings(gid1=null, gid2=null), parentId=t3_uw5c5r, permalink=/r/apolloapp/comments/uw5c5r/apollo_113_is_now_available_im_so_excited_about/i9pg80e/, repliesRaw=me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedCommentDataListing@67257c7, replies=[Comment(id=i9pqjs5, fullname=t1_i9pqjs5, allAwarding=[], author=BOSIG, body=Just updated, enabled the feature and tried it. But can’t get it to work, videos are blurry until the second loop. Anyone else? iPhone 11 Pro, iOS 15.5., bodyHtml=<div class="md"><p>Just updated, enabled the feature and tried it. But can’t get it to work, videos are blurry until the second loop. Anyone else? iPhone 11 Pro, iOS 15.5.</p>
        </div>, canGild=true, created=1653331415, createdUtc=1653331415, editedRaw=false, depth=1, distinguishedRaw=null, isArchived=false, isLocked=false, isSaved=false, isScoreHidden=false, isStickied=false, isSubmitter=false, likes=null, linkTitle=null, linkAuthor=null, linkId=t3_uw5c5r, linkUrl=null, linkPermalink=null, gildings=Gildings(gid1=null, gid2=null), parentId=t1_i9pg80e, permalink=/r/apolloapp/comments/uw5c5r/apollo_113_is_now_available_im_so_excited_about/i9pqjs5/, repliesRaw=me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedCommentDataListing@5831f4, replies=[Comment(id=i9ptplr, fullname=t1_i9ptplr, allAwarding=[], author=iamthatis, body=You're sure you're on 1.13 with the feature enabled? If so can you reply with a screen recording and a link to the post that's not loading high quality?, bodyHtml=<div class="md"><p>You&#39;re sure you&#39;re on 1.13 with the feature enabled? If so can you reply with a screen recording and a link to the post that&#39;s not loading high quality?</p>
        </div>, canGild=true, created=1653332772, createdUtc=1653332772, editedRaw=false, depth=2, distinguishedRaw=moderator, isArchived=false, isLocked=false, isSaved=false, isScoreHidden=false, isStickied=false, isSubmitter=true, likes=null, linkTitle=null, linkAuthor=null, linkId=t3_uw5c5r, linkUrl=null, linkPermalink=null, gildings=Gildings(gid1=null, gid2=null), parentId=t1_i9pqjs5, permalink=/r/apolloapp/comments/uw5c5r/apollo_113_is_now_available_im_so_excited_about/i9ptplr/, repliesRaw=me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedCommentDataListing@b7dcd1d, replies=[Comment(id=i9pxf4z, fullname=t1_i9pxf4z, allAwarding=[], author=Creamsicl3, body=Where is the setting?, bodyHtml=<div class="md"><p>Where is the setting?</p>
        </div>, canGild=true, created=1653334358, createdUtc=1653334358, editedRaw=false, depth=3, distinguishedRaw=null, isArchived=false, isLocked=false, isSaved=false, isScoreHidden=false, isStickied=false, isSubmitter=false, likes=null, linkTitle=null, linkAuthor=null, linkId=t3_uw5c5r, linkUrl=null, linkPermalink=null, gildings=Gildings(gid1=null, gid2=null), parentId=t1_i9ptplr, permalink=/r/apolloapp/comments/uw5c5r/apollo_113_is_now_available_im_so_excited_about/i9pxf4z/, repliesRaw=me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedCommentDataListing@a7d5b92, replies=[Comment(id=i9q2hpe, fullname=t1_i9q2hpe, allAwarding=[], author=2400xt, body=Settings > General > scroll down and it's under the "Media" section (I think mine was enabled by default), bodyHtml=<div class="md"><p>Settings &gt; General &gt; scroll down and it&#39;s under the &quot;Media&quot; section (I think mine was enabled by default)</p>
        </div>, canGild=true, created=1653336519, createdUtc=1653336519, editedRaw=false, depth=4, distinguishedRaw=null, isArchived=false, isLocked=false, isSaved=false, isScoreHidden=false, isStickied=false, isSubmitter=false, likes=null, linkTitle=null, linkAuthor=null, linkId=t3_uw5c5r, linkUrl=null, linkPermalink=null, gildings=Gildings(gid1=null, gid2=null), parentId=t1_i9pxf4z, permalink=/r/apolloapp/comments/uw5c5r/apollo_113_is_now_available_im_so_excited_about/i9q2hpe/, repliesRaw=null, replies=null, parentFullname=t1_i9pxf4z, score=30, subreddit=apolloapp, subredditId=t5_363lq, subredditNamePrefixed=r/apolloapp, isCollapsed=false, contentLinks=[])], parentFullname=t1_i9ptplr, score=20, subreddit=apolloapp, subredditId=t5_363lq, subredditNamePrefixed=r/apolloapp, isCollapsed=false, contentLinks=[]), Comment(id=i9q09uo, fullname=t1_i9q09uo, allAwarding=[], author=BOSIG, body=Here’s the post: https://reddit.com/r/discgolf/comments/uvv8gv/fpo_disc_golfer_jennifer_allen_has_an_interesting/
        Screen recording: https://streamable.com/i9snhp, bodyHtml=<div class="md"><p>Here’s the post: <a href="https://reddit.com/r/discgolf/comments/uvv8gv/fpo_disc_golfer_jennifer_allen_has_an_interesting/">https://reddit.com/r/discgolf/comments/uvv8gv/fpo_disc_golfer_jennifer_allen_has_an_interesting/</a>
        Screen recording: <a href="https://streamable.com/i9snhp">https://streamable.com/i9snhp</a></p>
        </div>, canGild=true, created=1653335572, createdUtc=1653335572, editedRaw=false, depth=3, distinguishedRaw=null, isArchived=false, isLocked=false, isSaved=false, isScoreHidden=false, isStickied=false, isSubmitter=false, likes=null, linkTitle=null, linkAuthor=null, linkId=t3_uw5c5r, linkUrl=null, linkPermalink=null, gildings=Gildings(gid1=null, gid2=null), parentId=t1_i9ptplr, permalink=/r/apolloapp/comments/uw5c5r/apollo_113_is_now_available_im_so_excited_about/i9q09uo/, repliesRaw=me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedCommentDataListing@e32a963, replies=[Comment(id=i9ra41h, fullname=t1_i9ra41h, allAwarding=[], author=iamthatis, body=Thank you! Can you leave that setting on and try restarting the app? It looks like at that point the video player had already been created so the deblurinator probably didn't get a chance to act, bodyHtml=<div class="md"><p>Thank you! Can you leave that setting on and try restarting the app? It looks like at that point the video player had already been created so the deblurinator probably didn&#39;t get a chance to act</p>
        </div>, canGild=true, created=1653357115, createdUtc=1653357115, editedRaw=false, depth=4, distinguishedRaw=null, isArchived=false, isLocked=false, isSaved=false, isScoreHidden=false, isStickied=false, isSubmitter=true, likes=null, linkTitle=null, linkAuthor=null, linkId=t3_uw5c5r, linkUrl=null, linkPermalink=null, gildings=Gildings(gid1=null, gid2=null), parentId=t1_i9q09uo, permalink=/r/apolloapp/comments/uw5c5r/apollo_113_is_now_available_im_so_excited_about/i9ra41h/, repliesRaw=me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedCommentDataListing@c07960, replies=[Comment(id=i9rwxlz, fullname=t1_i9rwxlz, allAwarding=[], author=BOSIG, body=It seems to work now! Although a bit wired, since I did restart the app with the setting enabled before posting here, just in case! Anyhow it seems to work now, really nice 👌, bodyHtml=<div class="md"><p>It seems to work now! Although a bit wired, since I did restart the app with the setting enabled before posting here, just in case! Anyhow it seems to work now, really nice 👌</p>
        </div>, canGild=true, created=1653370111, createdUtc=1653370111, editedRaw=false, depth=5, distinguishedRaw=null, isArchived=false, isLocked=false, isSaved=false, isScoreHidden=false, isStickied=false, isSubmitter=false, likes=null, linkTitle=null, linkAuthor=null, linkId=t3_uw5c5r, linkUrl=null, linkPermalink=null, gildings=Gildings(gid1=null, gid2=null), parentId=t1_i9ra41h, permalink=/r/apolloapp/comments/uw5c5r/apollo_113_is_now_available_im_so_excited_about/i9rwxlz/, repliesRaw=me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedCommentDataListing@72f1e19, replies=[Comment(id=ia0awb5, fullname=t1_ia0awb5, allAwarding=[], author=iamthatis, body=Yay glad to hear :D, bodyHtml=<div class="md"><p>Yay glad to hear :D</p>
        </div>, canGild=true, created=1653526273, createdUtc=1653526273, editedRaw=false, depth=6, distinguishedRaw=null, isArchived=false, isLocked=false, isSaved=false, isScoreHidden=false, isStickied=false, isSubmitter=true, likes=null, linkTitle=null, linkAuthor=null, linkId=t3_uw5c5r, linkUrl=null, linkPermalink=null, gildings=Gildings(gid1=null, gid2=null), parentId=t1_i9rwxlz, permalink=/r/apolloapp/comments/uw5c5r/apollo_113_is_now_available_im_so_excited_about/ia0awb5/, repliesRaw=null, replies=null, parentFullname=t1_i9rwxlz, score=5, subreddit=apolloapp, subredditId=t5_363lq, subredditNamePrefixed=r/apolloapp, isCollapsed=false, contentLinks=[])], parentFullname=t1_i9ra41h, score=8, subreddit=apolloapp, subredditId=t5_363lq, subredditNamePrefixed=r/apolloapp, isCollapsed=false, contentLinks=[])], parentFullname=t1_i9q09uo, score=12, subreddit=apolloapp, subredditId=t5_363lq, subredditNamePrefixed=r/apolloapp, isCollapsed=false, contentLinks=[]), MoreComments(id=i9qzl5e, fullname=t1_i9qzl5e, count=2, depth=4, parentId=t1_i9q09uo, children=[i9qzl5e, i9q40x8], parentFullname=t1_i9q09uo, replies=null, hasReplies=false, repliesSize=0, isCollapsed=false, contentLinks=null)], parentFullname=t1_i9ptplr, score=6, subreddit=apolloapp, subredditId=t5_363lq, subredditNamePrefixed=r/apolloapp, isCollapsed=false, contentLinks=[WebLink(url=https://reddit.com/r/discgolf/comments/uvv8gv/fpo_disc_golfer_jennifer_allen_has_an_interesting/), WebLink(url=https://streamable.com/i9snhp)]), MoreComments(id=ia0hbjk, fullname=t1_ia0hbjk, count=2, depth=3, parentId=t1_i9ptplr, children=[ia0hbjk, iadc24d], parentFullname=t1_i9ptplr, replies=null, hasReplies=false, repliesSize=0, isCollapsed=false, contentLinks=null)], parentFullname=t1_i9pqjs5, score=57, subreddit=apolloapp, subredditId=t5_363lq, subredditNamePrefixed=r/apolloapp, isCollapsed=false, contentLinks=[])], parentFullname=t1_i9pg80e, score=64, subreddit=apolloapp, subredditId=t5_363lq, subredditNamePrefixed=r/apolloapp, isCollapsed=false, contentLinks=[]), Comment(id=i9qh27j, fullname=t1_i9qh27j, allAwarding=[], author=HashKing, body=So much this!!!

        It works great too.

        Noticed it within minutes of using the updated app!, bodyHtml=<div class="md"><p>So much this!!!</p>

        <p>It works great too.</p>

        <p>Noticed it within minutes of using the updated app!</p>
        </div>""", canGild=true, created=1653343005, createdUtc=1653343005, editedRaw=false, depth=1, distinguishedRaw=null, isArchived=false, isLocked=false, isSaved=false, isScoreHidden=false, isStickied=false, isSubmitter=false, likes=null, linkTitle=null, linkAuthor=null, linkId="t3_uw5c5r", linkUrl=null, linkPermalink=null, gildings= Gildings(gid1=null, gid2=null), parentId="t1_i9pg80e", permalink="/r/apolloapp/comments/uw5c5r/apollo_113_is_now_available_im_so_excited_about/i9qh27j/", repliesRaw=null, replies=null, parentFullname="t1_i9pg80e", score=4, subreddit="apolloapp", subredditId="t5_363lq", subredditNamePrefixed="r/apolloapp", isCollapsed=false, contentLinks= emptyList(), MoreComments(id="iadc5te", fullname="t1_iadc5te", count=2, depth=1, parentId="t1_i9pg80e", children= listOf("iadc5te", "i9rtk8z"), parentFullname="t1_i9pg80e", replies=null, hasReplies=false, repliesSize=0, isCollapsed=false, contentLinks=null), parentFullname=t3_uw5c5r, score=313, subreddit=apolloapp, subredditId=t5_363lq, subredditNamePrefixed=r/apolloapp, isCollapsed=false, contentLinks=[])
    }
}