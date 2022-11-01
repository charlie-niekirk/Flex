package me.cniekirk.flex.ui.util

import android.graphics.BitmapFactory
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File
import java.io.IOException

//@Parcelize
//data class Size2(val width: Int, val height: Int) : Parcelable
//
//class BitmapSizeDecoder : ResourceDecoder<File, BitmapFactory.Options> {
//    @Throws(IOException::class)
//    override fun handles(file: File, options: Options): Boolean {
//        return true
//    }
//
//    override fun decode(file: File, width: Int, height: Int, options: Options): Resource<BitmapFactory.Options>? {
//        val bmOptions: BitmapFactory.Options = BitmapFactory.Options()
//        bmOptions.inJustDecodeBounds = true
//        BitmapFactory.decodeFile(file.absolutePath, bmOptions)
//        return SimpleResource(bmOptions)
//    }
//}
//
//class OptionsSizeResourceTranscoder : ResourceTranscoder<BitmapFactory.Options, Size2> {
//    override fun transcode(resource: Resource<BitmapFactory.Options>, options: Options): Resource<Size2> {
//        val bmOptions = resource.get()
//        val size = Size2(bmOptions.outWidth, bmOptions.outHeight)
//        return SimpleResource(size)
//    }
//}