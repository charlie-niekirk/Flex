package me.cniekirk.flex.data.remote

import me.cniekirk.flex.data.remote.model.strapi.RpanResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface StrapiApi {

    @GET("broadcasts")
    suspend fun getRpanInfo(
        @Query("page_size") pageSize: Int = 1
    ): RpanResponse

}