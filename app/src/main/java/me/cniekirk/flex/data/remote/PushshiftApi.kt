package me.cniekirk.flex.data.remote

import me.cniekirk.flex.data.remote.model.pushshift.DeletedComments
import retrofit2.http.GET
import retrofit2.http.Query

interface PushshiftApi {

    @GET("reddit/comment/search")
    suspend fun getDeletedComment(
        @Query("ids") ids: String
    ): DeletedComments
}