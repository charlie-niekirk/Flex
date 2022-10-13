package me.cniekirk.flex.di

import android.content.Context
import android.view.View
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import im.ene.toro.exoplayer.Config
import im.ene.toro.exoplayer.ExoCreator
import im.ene.toro.exoplayer.MediaSourceBuilder
import im.ene.toro.exoplayer.ToroExo
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.LinkResolverDef
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.recycler.MarkwonAdapter
import io.noties.markwon.recycler.table.TableEntry
import io.noties.markwon.recycler.table.TableEntryPlugin
import io.noties.markwon.utils.Dip
import me.cniekirk.flex.R
import me.cniekirk.flex.util.Link
import me.cniekirk.flex.util.processLink
import me.cniekirk.flex.util.video.LoopExoCreator
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.commonmark.ext.gfm.tables.TableBlock
import java.io.File
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class MiscModule {

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
    fun provideSimpleCache(@ApplicationContext context: Context): SimpleCache {
        return SimpleCache(
            File(context.cacheDir, "/exoplayer"),
            LeastRecentlyUsedCacheEvictor(209715200L), ExoDatabaseProvider(context)
        )
    }

    @Provides
    @Singleton
    fun provideExoCreator(@ApplicationContext context: Context, simpleCache: SimpleCache): ExoCreator {
        val config = Config.Builder(context).setMediaSourceBuilder(MediaSourceBuilder.DEFAULT)
            .setCache(simpleCache)
            .build()
        return LoopExoCreator(ToroExo.with(context), config)
    }

    @Provides
    @Singleton
    fun provideMarkwon(@ApplicationContext context: Context): Markwon {
        return Markwon
            .builder(context)
            .usePlugin(StrikethroughPlugin())
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(TableEntryPlugin.create { builder ->
                val dip = Dip.create(context)
                builder
                    .tableBorderColor(context.resources.getColor(R.color.table_border, null))
                    .tableHeaderRowBackgroundColor(context.resources.getColor(R.color.table_border, null))
                    .tableCellPadding(dip.toPx(4))
                    .tableBorderWidth(dip.toPx(1))
                    .build()
            })
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                    builder.linkResolver(object : LinkResolverDef() {
                        override fun resolve(view: View, link: String) {
                            when (link.processLink()) {
                                is Link.ImgurGalleryLink -> {

                                }
                                else -> {
                                    super.resolve(view, link)
                                }
                            }
                        }
                    })
                }
            })
            //.usePlugin(SyntaxHighlightPlugin.create(Prism4j(GrammarLocatorDef()), Prism4jThemeDefault()))
            .build()
    }

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