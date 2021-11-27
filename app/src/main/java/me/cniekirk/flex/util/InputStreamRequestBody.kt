package me.cniekirk.flex.util

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.IOException
import okio.source

/**
 * [RequestBody] implementation that opens a [Uri] and writes the
 * stream to the [BufferedSink]
 */
class InputStreamRequestBody(
    private val contentResolver: ContentResolver,
    private val uri: Uri) : RequestBody() {

    override fun contentType(): MediaType? {
        val contentType = contentResolver.getType(uri)
        return contentType?.toMediaTypeOrNull()
    }

    override fun writeTo(sink: BufferedSink) {
        val input = contentResolver.openInputStream(uri)
        input?.use { sink.writeAll(it.source()) }
            ?: throw IOException("Could not process URI")
    }
}