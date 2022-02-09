package me.cniekirk.flex.data.remote

import me.cniekirk.flex.data.remote.model.imgur.Data
import me.cniekirk.flex.data.remote.model.imgur.ImageData
import me.cniekirk.flex.data.remote.model.imgur.ImgurResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface ImgurApi {

    @Multipart
    @POST("3/upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part? = null,
        @Header("Authorization") authorization: String = "Client-ID a3a83e144ed48df"
    ): ImgurResponse<Data>

    @GET("3/album/{albumHash}/images")
    suspend fun getGalleryImages(
        @Path("albumHash") albumHash: String,
        @Header("Authorization") authorization: String = "Client-ID a3a83e144ed48df"
    ): ImgurResponse<List<ImageData>>

}