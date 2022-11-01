package me.cniekirk.flex

import android.content.Context
import android.view.View
import coil.ImageLoader
import coil.request.ImageRequest
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import im.ene.toro.exoplayer.Config
import im.ene.toro.exoplayer.ExoCreator
import im.ene.toro.exoplayer.MediaSourceBuilder
import im.ene.toro.exoplayer.ToroExo
import me.cniekirk.flex.di.MiscModule
import me.cniekirk.flex.util.Link
import me.cniekirk.flex.util.processLink
import me.cniekirk.flex.util.video.LoopExoCreator
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [MiscModule::class]
)
class FakeMiscModule {

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context).build()
    }

    @Provides
    @Singleton
    fun provideImageRequest(@ApplicationContext context: Context): ImageRequest.Builder {
        return ImageRequest.Builder(context)
    }

    object NullRepliesInterceptor : Interceptor {

        private const val TAG_BEFORE = "\"replies\": \"\""
        private const val TAG_AFTER = "\"replies\": null"

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)

            var rawJson = response.body?.string() ?: ""

            if (rawJson.contains(TAG_BEFORE)) {
                rawJson = rawJson.replace(TAG_BEFORE, TAG_AFTER)
            }

            return response.newBuilder()
                .body(rawJson.toResponseBody(response.body?.contentType())).build()
        }
    }

    @Provides
    @Singleton
    fun provideExoCreator(@ApplicationContext context: Context): ExoCreator {
        val config = Config.Builder(context).setMediaSourceBuilder(MediaSourceBuilder.DEFAULT)
            .build()
        return LoopExoCreator(ToroExo.with(context), config)
    }

//    @Provides
//    @Singleton
//    fun provideMarkwon(@ApplicationContext context: Context): Markwon {
//        return Markwon
//            .builder(context)
//            .usePlugin(StrikethroughPlugin())
//            .usePlugin(LinkifyPlugin.create())
//            .usePlugin(TableEntryPlugin.create { builder ->
//                val dip = Dip.create(context)
//                builder
//                    .tableBorderColor(context.resources.getColor(R.color.table_border, null))
//                    .tableHeaderRowBackgroundColor(context.resources.getColor(R.color.table_border, null))
//                    .tableCellPadding(dip.toPx(4))
//                    .tableBorderWidth(dip.toPx(1))
//                    .build()
//            })
//            .usePlugin(object : AbstractMarkwonPlugin() {
//                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
//                    builder.linkResolver(object : LinkResolverDef() {
//                        override fun resolve(view: View, link: String) {
//                            when (link.processLink()) {
//                                is Link.ImgurGalleryLink -> {
//
//                                }
//                                else -> {
//                                    super.resolve(view, link)
//                                }
//                            }
//                        }
//                    })
//                }
//            })
//            .build()
//    }

//    @Provides
//    @Singleton
//    fun provideMarkwonAdapter(): MarkwonAdapter {
//        return MarkwonAdapter.builder(R.layout.adapter_default_entry, R.id.text_default)
//            .include(TableBlock::class.java, TableEntry.create {
//                it.tableLayout(R.layout.adapter_table_block, R.id.table_layout)
//                    .textLayoutIsRoot(R.layout.view_table_entry_cell)
//            }).build()
//    }
}