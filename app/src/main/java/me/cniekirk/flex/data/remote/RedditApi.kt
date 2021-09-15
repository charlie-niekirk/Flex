package me.cniekirk.flex.data.remote

import me.cniekirk.flex.data.remote.model.*
import me.cniekirk.flex.data.remote.model.auth.ScopesWrapper
import me.cniekirk.flex.data.remote.model.auth.Token
import me.cniekirk.flex.data.remote.model.envelopes.EnvelopedContributionListing
import retrofit2.http.*
import retrofit2.http.FieldMap
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.FormUrlEncoded

interface RedditApi {

    @GET("r/{subreddit}{sortType}.json?raw_json=1")
    suspend fun getPosts(
        @Path("subreddit") subreddit: String,
        @Path("sortType") sortType: String,
        @Query("after") after: String? = null,
        @Query("before") before: String? = null,
        @Query("count") count: Int? = null,
        @Query("limit") limit: Int = 15): RedditResponse<Listing<Submission>>

    @GET("/{sort}.json?raw_json=1")
    suspend fun getFrontpagePosts(@Path("sort") sort: String): RedditResponse<Listing<Submission>>

    @GET("/comments/{id}{sortType}.json?raw_json=1")
    suspend fun getCommentsForListing(
        @Path("id") id: String,
        @Path("sortType") sortType: String): List<EnvelopedContributionListing>

    @GET("api/v1/scopes")
    suspend fun getScopes(): ScopesWrapper

    @FormUrlEncoded
    @POST("api/v1/access_token")
    suspend fun getAccessToken(
        @HeaderMap headers: Map<String, String>,
        @FieldMap params: Map<String, String>): Token

    @POST("api/v1/access_token")
    suspend fun renewToken(
        @HeaderMap header: HashMap<String, String>,
        @Query("grant_type") grantType: String = "refresh_token",
        @Query("refresh_token") refreshToken: String): Token

    @POST("api/v1/revoke_token")
    suspend fun revoke(
        @HeaderMap header: HashMap<String, String>,
        @Query("token") token: String,
        @Query("token_type_hint") tokenTypeHint: String)

}