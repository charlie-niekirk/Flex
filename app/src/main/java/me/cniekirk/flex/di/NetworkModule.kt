package me.cniekirk.flex.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.cniekirk.flex.BuildConfig
import me.cniekirk.flex.data.local.db.dao.PreLoginUserDao
import me.cniekirk.flex.data.local.db.dao.UserDao
import me.cniekirk.flex.data.local.repo.LocalDataRepositoryImpl
import me.cniekirk.flex.data.remote.*
import me.cniekirk.flex.data.remote.model.reddit.auth.LoggedInAuthenticator
import me.cniekirk.flex.data.remote.model.reddit.auth.PreLoginAuthenticator
import me.cniekirk.flex.data.remote.model.reddit.base.EnvelopeKind
import me.cniekirk.flex.data.remote.model.reddit.envelopes.*
import me.cniekirk.flex.data.remote.model.reddit.util.ForceToBooleanJsonAdapter
import me.cniekirk.flex.data.remote.repo.ImgurDataRepositoryImpl
import me.cniekirk.flex.data.remote.repo.RedditDataRepositoryImpl
import me.cniekirk.flex.domain.ImgurDataRepository
import me.cniekirk.flex.domain.LocalDataRepository
import me.cniekirk.flex.domain.RedditDataRepository
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        return Cache(context.cacheDir, 102400L)
    }

    @Provides
    @Named("preLogin")
    @Singleton
    fun providePreLoginOkHttp(cache: Cache,
                              @Named("preAuthApi") redditApi: RedditApi,
                              preLoginUserDao: PreLoginUserDao
    ): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(logger)
                .addInterceptor(MiscModule.NullRepliesInterceptor)
                .authenticator(PreLoginAuthenticator(redditApi, preLoginUserDao))
                .build()
        } else {
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(MiscModule.NullRepliesInterceptor)
                .authenticator(PreLoginAuthenticator(redditApi, preLoginUserDao))
                .build()
        }
    }

    @Provides
    @Named("preAuth")
    @Singleton
    fun provideOkHttp(cache: Cache): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(logger)
                .addInterceptor(MiscModule.NullRepliesInterceptor)
                .build()
        } else {
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(MiscModule.NullRepliesInterceptor)
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
                .addInterceptor(MiscModule.NullRepliesInterceptor)
                .authenticator(LoggedInAuthenticator(redditApi, userDao))
                .build()
        } else {
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(MiscModule.NullRepliesInterceptor)
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
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(
                PolymorphicJsonAdapterFactory.of(EnvelopedData::class.java, "kind")
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
            )
            .add(ForceToBooleanJsonAdapter).build()
    }

    @Provides
    @Singleton
    @Named("twitter")
    fun provideTwitterRetrofit(@Named("preAuth") okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.twitter.com/2/")
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
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
    fun provideLocalDataRepo(localDataRepositoryImpl: LocalDataRepositoryImpl)
            : LocalDataRepository = localDataRepositoryImpl

    @Provides
    @Singleton
    fun provideImgurDataRepo(imgurDataRepositoryImpl: ImgurDataRepositoryImpl)
            : ImgurDataRepository = imgurDataRepositoryImpl

    @Provides
    @Singleton
    fun provideTwitterApi(@Named("twitter") retrofit: Retrofit): TwitterApi {
        return retrofit.create(TwitterApi::class.java)
    }
}