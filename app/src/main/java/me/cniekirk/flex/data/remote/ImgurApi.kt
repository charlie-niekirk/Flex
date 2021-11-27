package me.cniekirk.flex.data.remote

import me.cniekirk.flex.data.remote.imgur.ImgurResponse
import okhttp3.MultipartBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImgurApi {

    @Multipart
    @POST("3/upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part? = null,
        @Header("Client-ID") clientId: String = "a3a83e144ed48df"
    ): ImgurResponse

}