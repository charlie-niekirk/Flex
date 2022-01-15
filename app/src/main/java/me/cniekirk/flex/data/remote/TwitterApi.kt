package me.cniekirk.flex.data.remote

import me.cniekirk.flex.data.remote.model.twitter.TweetResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface TwitterApi {

    @GET("tweets/{tweet_id}")
    suspend fun getTweet(
        @Path("tweet_id") tweetId: String,
        @Header("Authorization") bearer: String = "Bearer AAAAAAAAAAAAAAAAAAAAAAtvYAEAAAAAdk1wrZ6vXUPUvyOECpeqDUp60WU%3DUlRPUonqQuCljHWaHXHa4OLmUCmTyeRrrNANcKUbubLg6V3B3a",
        @Query("expansions") expansions: String = "referenced_tweets.id.author_id,attachments.media_keys",
        @Query("user.fields") userFields: String = "name,profile_image_url,created_at,verified",
        @Query("media.fields") mediaFields: String = "url"
    ): TweetResponse

}