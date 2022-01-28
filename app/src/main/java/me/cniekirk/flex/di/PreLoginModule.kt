package me.cniekirk.flex.di

import android.content.Context
import androidx.room.Room
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import im.ene.toro.exoplayer.Config
import im.ene.toro.exoplayer.ExoCreator
import im.ene.toro.exoplayer.MediaSourceBuilder
import im.ene.toro.exoplayer.ToroExo
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tables.TableTheme
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.recycler.MarkwonAdapter
import io.noties.markwon.recycler.table.TableEntry
import io.noties.markwon.recycler.table.TableEntryPlugin
import io.noties.markwon.syntax.Prism4jThemeDefault
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.markwon.utils.Dip
import io.noties.prism4j.GrammarImpl
import io.noties.prism4j.Prism4j
import me.cniekirk.flex.BuildConfig
import me.cniekirk.flex.R
import me.cniekirk.flex.data.local.db.AppDatabase
import me.cniekirk.flex.data.local.db.dao.PreLoginUserDao
import me.cniekirk.flex.data.local.db.dao.UserDao
import me.cniekirk.flex.data.remote.*
import me.cniekirk.flex.data.remote.model.reddit.auth.LoggedInAuthenticator
import me.cniekirk.flex.data.remote.model.reddit.auth.PreLoginAuthenticator
import me.cniekirk.flex.data.remote.model.reddit.base.EnvelopeKind
import me.cniekirk.flex.data.remote.model.reddit.envelopes.*
import me.cniekirk.flex.data.remote.model.reddit.util.ForceToBooleanJsonAdapter
import me.cniekirk.flex.data.remote.repo.ImgurDataRepositoryImpl
import me.cniekirk.flex.data.remote.repo.RedditDataRepositoryImpl
import me.cniekirk.flex.domain.ImgurDataRepository
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.util.resolveColorAttr
import me.cniekirk.flex.util.video.LoopExoCreator
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.commonmark.ext.gfm.tables.TableBlock
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class PreLoginModule {

    @Provides
    @Named("preLogin")
    @Singleton
    fun providePreLoginOkHttp(cache: Cache,
                              @Named("preAuthApi") redditApi: RedditApi,
                              preLoginUserDao: PreLoginUserDao): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(logger)
                .addInterceptor(NullRepliesInterceptor)
                .authenticator(PreLoginAuthenticator(redditApi, preLoginUserDao))
                .build()
        } else {
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(NullRepliesInterceptor)
                .authenticator(PreLoginAuthenticator(redditApi, preLoginUserDao))
                .build()
        }
    }

    @Provides
    @Named("postLogin")
    @Singleton
    fun provideAuthedOkHttp(cache: Cache,
                            @Named("preAuthApi") redditApi: RedditApi,
                            userDao: UserDao
    ): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(logger)
                .addInterceptor(NullRepliesInterceptor)
                .authenticator(LoggedInAuthenticator(redditApi, userDao))
                .build()
        } else {
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(NullRepliesInterceptor)
                .authenticator(LoggedInAuthenticator(redditApi, userDao))
                .build()
        }
    }

    @Provides
    @Named("download")
    @Singleton
    fun provideDownloadOkHttp(cache: Cache): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(logger)
                .build()
        } else {
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .build()
        }
    }

    @Provides
    @Named("imgurRetrofit")
    @Singleton
    fun provideImgurRetrofit(@Named("preAuth") okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://api.imgur.com/")
            .build()
    }

    @Provides
    @Named("wikiRetrofit")
    @Singleton
    fun provideWikiRetrofit(@Named("preAuth") okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://en.wikipedia.org/api/rest_v1/")
            .build()
    }

    @Provides
    @Named("userlessRetrofit")
    @Singleton
    fun provideRetrofit(@Named("preAuth") okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://www.reddit.com/")
            .build()
    }

    @Provides
    @Named("preLoginRetrofit")
    @Singleton
    fun providePreLoginRetrofit(@Named("preLogin") okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://oauth.reddit.com/")
            .build()
    }

    @Provides
    @Named("redGifRetrofit")
    @Singleton
    fun provideRedGifRetrofit(@Named("preLogin") okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://api.redgifs.com/")
            .build()
    }

    @Provides
    @Named("gfycatRetrofit")
    @Singleton
    fun provideGfycatRetrofit(@Named("preLogin") okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://api.gfycat.com/")
            .build()
    }

    @Provides
    @Named("authRetrofit")
    @Singleton
    fun provideAuthedRetrofit(@Named("postLogin") okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://oauth.reddit.com/")
            .build()
    }

    @Provides
    @Named("downloadRetrofit")
    @Singleton
    fun provideDownloadRetrofit(@Named("download") okHttpClient: Lazy<OkHttpClient>): Retrofit {
        return Retrofit.Builder()
            .callFactory { okHttpClient.get().newCall(it) }
            .baseUrl("https://oauth.reddit.com/")
            .build()
    }

    @Provides
    @Named("streamableRetrofit")
    @Singleton
    fun provideStreamableRetrofit(@Named("preLogin") okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://api.streamable.com/")
            .build()
    }

    @Provides
    @Named("preAuthApi")
    @Singleton
    fun providePreAuthRedditApi(@Named("userlessRetrofit") retrofit: Retrofit): RedditApi
        = retrofit.create(RedditApi::class.java)

    @Provides
    @Named("preLoginApi")
    @Singleton
    fun providePreLoginRedditApi(@Named("preLoginRetrofit") retrofit: Retrofit): RedditApi
            = retrofit.create(RedditApi::class.java)

    @Provides
    @Named("loginApi")
    @Singleton
    fun provideAuthedRedditApi(@Named("authRetrofit") retrofit: Retrofit): RedditApi
            = retrofit.create(RedditApi::class.java)

    @Provides
    @Named("downloadApi")
    @Singleton
    fun provideDownloadRedditApi(@Named("downloadRetrofit") retrofit: Retrofit): RedditApi
            = retrofit.create(RedditApi::class.java)

    @Provides
    @Singleton
    fun provideRedGifsApi(@Named("redGifRetrofit") retrofit: Retrofit): RedGifsApi
            = retrofit.create(RedGifsApi::class.java)

    @Provides
    @Singleton
    fun provideGfycatApi(@Named("gfycatRetrofit") retrofit: Retrofit): GfycatApi
            = retrofit.create(GfycatApi::class.java)

    @Provides
    @Singleton
    fun provideStreamableApi(@Named("streamableRetrofit") retrofit: Retrofit): StreamableApi
            = retrofit.create(StreamableApi::class.java)

    @Provides
    @Singleton
    fun provideImgurApi(@Named("imgurRetrofit") retrofit: Retrofit): ImgurApi
            = retrofit.create(ImgurApi::class.java)

    @Provides
    @Singleton
    fun provideWikiApi(@Named("wikiRetrofit") retrofit: Retrofit): WikipediaApi
            = retrofit.create(WikipediaApi::class.java)

    @Provides
    @Singleton
    fun provideRedditDataRepo(redditDataRepositoryImpl: RedditDataRepositoryImpl)
            : RedditDataRepository = redditDataRepositoryImpl

    @Provides
    @Singleton
    fun provideImgurDataRepo(imgurDataRepositoryImpl: ImgurDataRepositoryImpl)
            : ImgurDataRepository = imgurDataRepositoryImpl

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
            //.usePlugin(SyntaxHighlightPlugin.create(Prism4j(GrammarLocatorDef()), Prism4jThemeDefault()))
            .build()
    }

    @Provides
    @Singleton
    fun provideMarkwonAdapter(): MarkwonAdapter {
        return MarkwonAdapter.builder(R.layout.adapter_default_entry, R.id.text_default)
            .include(TableBlock::class.java, TableEntry.create {
                it.tableLayout(R.layout.adapter_table_block, R.id.table_layout)
                    .textLayoutIsRoot(R.layout.view_table_entry_cell)
            }).build()
    }

}