package me.cniekirk.flex

//import android.content.Context
//import android.graphics.BitmapFactory
//import com.bumptech.glide.Glide
//import com.bumptech.glide.Registry
//import com.bumptech.glide.annotation.GlideModule
//import com.bumptech.glide.module.AppGlideModule
//import me.cniekirk.flex.ui.util.BitmapSizeDecoder
//import me.cniekirk.flex.ui.util.OptionsSizeResourceTranscoder
//import me.cniekirk.flex.ui.util.Size2
//import java.io.File

//@GlideModule
//class FlexGlideModule : AppGlideModule() {
//
//    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
//        registry.prepend(File::class.java, BitmapFactory.Options::class.java, BitmapSizeDecoder())
//        registry.register(BitmapFactory.Options::class.java, Size2::class.java, OptionsSizeResourceTranscoder())
//    }
//}