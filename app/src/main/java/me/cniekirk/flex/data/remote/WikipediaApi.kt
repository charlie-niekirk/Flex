package me.cniekirk.flex.data.remote

import me.cniekirk.flex.data.remote.model.wikipedia.WikiSummary
import retrofit2.http.GET
import retrofit2.http.Path

interface WikipediaApi {

    @GET("page/summary/{article}")
    suspend fun getWikiArticleDetails(
        @Path("article") article: String
    ) : WikiSummary
}