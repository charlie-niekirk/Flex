package me.cniekirk.flex.di

import android.content.Context
import androidx.room.Room
import coil.ImageLoader
import coil.request.ImageRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.cniekirk.flex.BuildConfig
import me.cniekirk.flex.data.local.db.AppDatabase
import me.cniekirk.flex.data.local.db.UserDao
import me.cniekirk.flex.data.local.prefs.Preferences
import me.cniekirk.flex.data.remote.GfycatApi
import me.cniekirk.flex.data.remote.RedGifsApi
import me.cniekirk.flex.data.remote.RedditApi
import me.cniekirk.flex.data.remote.StreamableApi
import me.cniekirk.flex.data.remote.auth.AccessTokenAuthenticator
import me.cniekirk.flex.data.remote.model.base.EnvelopeKind
import me.cniekirk.flex.data.remote.model.envelopes.*
import me.cniekirk.flex.data.remote.repo.MediaResolutionRepositoryImpl
import me.cniekirk.flex.data.remote.repo.RedditDataRepositoryImpl
import me.cniekirk.flex.domain.MediaResolutionRepository
import me.cniekirk.flex.domain.RedditDataRepository
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PreLoginModule {
    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        return Cache(context.cacheDir, 102400L)
    }

    @Provides
    @Named("preLogin")
    @Singleton
    fun provideOkHttp(cache: Cache): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(logger)
                .addInterceptor(NullRepliesInterceptor)
                .build()
        } else {
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(NullRepliesInterceptor)
                .build()
        }
    }

    @Provides
    @Named("postLogin")
    @Singleton
    fun provideAuthedOkHttp(cache: Cache,
                            @Named("userlessApi") redditApi: RedditApi,
                            userDao: UserDao
    ): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(logger)
                .addInterceptor(NullRepliesInterceptor)
                .authenticator(AccessTokenAuthenticator(redditApi, userDao))
                .build()
        } else {
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(NullRepliesInterceptor)
                .authenticator(AccessTokenAuthenticator(redditApi, userDao))
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
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(PolymorphicJsonAdapterFactory.of(EnvelopedData::class.java, "kind")
                    .withSubtype(EnvelopedComment::class.java, EnvelopeKind.Comment.value)
                    .withSubtype(EnvelopedMoreComment::class.java, EnvelopeKind.More.value)
                    .withSubtype(EnvelopedSubmission::class.java, EnvelopeKind.Link.value)
            ).add(
                PolymorphicJsonAdapterFactory.of(EnvelopedContribution::class.java, "kind")
                    .withSubtype(EnvelopedSubmission::class.java, EnvelopeKind.Link.value)
                    .withSubtype(EnvelopedComment::class.java, EnvelopeKind.Comment.value)
                    .withSubtype(EnvelopedMoreComment::class.java, EnvelopeKind.More.value)
            ).add(
                PolymorphicJsonAdapterFactory.of(EnvelopedCommentData::class.java, "kind")
                    .withSubtype(EnvelopedComment::class.java, EnvelopeKind.Comment.value)
                    .withSubtype(EnvelopedMoreComment::class.java, EnvelopeKind.More.value)
            ).build()
    }

    @Provides
    @Named("userlessRetrofit")
    @Singleton
    fun provideRetrofit(@Named("preLogin") okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://www.reddit.com/")
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
    @Named("userlessApi")
    @Singleton
    fun provideRedditApi(@Named("userlessRetrofit") retrofit: Retrofit): RedditApi
        = retrofit.create(RedditApi::class.java)

    @Provides
    @Named("authApi")
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
    fun provideRedditDataRepo(redditDataRepositoryImpl: RedditDataRepositoryImpl)
            : RedditDataRepository = redditDataRepositoryImpl

    @Provides
    @Singleton
    fun provideMediaResolutionRepo(mediaResolutionRepositoryImpl: MediaResolutionRepositoryImpl)
            : MediaResolutionRepository = mediaResolutionRepositoryImpl

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "app-database")
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()

    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext context: Context): Preferences = Preferences(context)

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
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context).build()
    }

    @Provides
    @Singleton
    fun provideImageRequest(@ApplicationContext context: Context): ImageRequest.Builder {
        return ImageRequest.Builder(context)
    }

}