package me.cniekirk.flex.data.remote

import me.cniekirk.flex.data.remote.model.Listing
import me.cniekirk.flex.data.remote.model.RedditResponse
import me.cniekirk.flex.data.remote.model.auth.ScopesWrapper
import me.cniekirk.flex.data.remote.model.auth.Token
import retrofit2.http.*
import retrofit2.http.FieldMap
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.FormUrlEncoded

interface RedditApi {

    @GET("r/{subreddit}/hot.json")
    suspend fun getTopPosts(@Path("subreddit") subreddit: String): RedditResponse<Listing>

    @GET("/{sort}.json")
    suspend fun getFrontpagePosts(@Path("sort") sort: String): RedditResponse<Listing>

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