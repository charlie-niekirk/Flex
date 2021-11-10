package me.cniekirk.flex.data.remote

import android.util.Base64
import me.cniekirk.flex.data.remote.model.*
import me.cniekirk.flex.data.remote.model.auth.ScopesWrapper
import me.cniekirk.flex.data.remote.model.auth.Token
import me.cniekirk.flex.data.remote.model.base.UserList
import me.cniekirk.flex.data.remote.model.envelopes.EnvelopedContributionListing
import me.cniekirk.flex.data.remote.model.rules.Rules
import me.cniekirk.flex.data.remote.model.subreddit.ModUser
import me.cniekirk.flex.data.remote.model.subreddit.Subreddit
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface RedditApi {

    @GET("r/{subreddit}{sortType}.json?raw_json=1")
    suspend fun getPosts(
        @Path("subreddit") subreddit: String,
        @Path("sortType") sortType: String,
        @Query("after") after: String? = null,
        @Query("before") before: String? = null,
        @Query("count") count: Int? = null,
        @Query("limit") limit: Int = 15,
        @Header("Authorization") authorization: String? = null): RedditResponse<Listing<AuthedSubmission>>

    @GET("/{sort}.json?raw_json=1")
    suspend fun getFrontpagePosts(@Path("sort") sort: String): RedditResponse<Listing<Submission>>

    @GET("/comments/{id}{sortType}.json?raw_json=1")
    suspend fun getCommentsForListing(
        @Path("id") id: String,
        @Path("sortType") sortType: String,
        @Header("Authorization") authorization: String? = null): List<EnvelopedContributionListing>

    @GET("/api/morechildren")
    suspend fun getMoreComments(
        @Query("api_type") apiType: String = "json",
        @Query("link_id") linkId: String,
        @Query("children") children: String,
        @Header("Authorization") authorization: String? = null): MoreChildrenResponse

    @GET("api/v1/scopes")
    suspend fun getScopes(): ScopesWrapper

    @FormUrlEncoded
    @POST("api/v1/access_token")
    suspend fun getAccessToken(
        @HeaderMap headers: Map<String, String>,
        @FieldMap params: Map<String, String>): Token

    @FormUrlEncoded
    @POST("api/v1/access_token")
    suspend fun getUserlessAccessToken(
        @Header("Authorization") authorization: String = "Basic ${Base64.encodeToString("14spBPt-yu7A6NJ7tdByhg:".toByteArray(), Base64.NO_WRAP)}",
        @Field("grant_type") grantType: String = "https://oauth.reddit.com/grants/installed_client",
        @Field("device_id") deviceId: String): Token

    @POST("api/v1/access_token")
    suspend fun renewToken(
        @HeaderMap header: Map<String, String>,
        @Query("grant_type") grantType: String = "refresh_token",
        @Query("refresh_token") refreshToken: String): Token

    @POST("api/v1/revoke_token")
    suspend fun revoke(
        @HeaderMap header: HashMap<String, String>,
        @Query("token") token: String,
        @Query("token_type_hint") tokenTypeHint: String)

    @POST("api/vote")
    @FormUrlEncoded
    suspend fun vote(
        @Header("Authorization") authorization: String,
        @Field("id") thingId: String,
        @Field("dir") voteDir: Int
    )

    @GET("subreddits/search.json?raw_json=1")
    suspend fun searchSubreddits(
        @Header("Authorization") authorization: String,
        @Query("q") query: String,
        @Query("sort") sort: String,
        @Query("include_over_18") nsfw: Boolean
    ): RedditResponse<Listing<Subreddit>>

    @GET("r/{subreddit}/about/rules.json?raw_json=1")
    suspend fun getSubredditRules(
        @Header("Authorization") authorization: String,
        @Path("subreddit") subreddit: String
    ): Rules

    @GET("r/{subreddit}/about.json?raw_json=1")
    suspend fun getSubredditInfo(
        @Header("Authorization") authorization: String,
        @Path("subreddit") subreddit: String
    ): RedditResponse<Subreddit>

    @GET("r/{subreddit}/about/moderators.json?raw_json=1")
    suspend fun getSubredditModerators(
        @Header("Authorization") authorization: String,
        @Path("subreddit") subreddit: String
    ): UserList<ModUser>

    @GET
    @Streaming
    suspend fun downloadMedia(@Url url: String): Response<ResponseBody>

}