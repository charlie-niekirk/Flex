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
import me.cniekirk.flex.data.remote.RedditApi
import me.cniekirk.flex.data.remote.model.base.EnvelopeKind
import me.cniekirk.flex.data.remote.model.envelopes.*
import me.cniekirk.flex.data.remote.repo.RedditDataRepositoryImpl
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
    @Singleton
    fun provideRetrofit(okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://www.reddit.com/")
            .build()
    }

    @Provides
    @Singleton
    fun provideRedditApi(retrofit: Retrofit): RedditApi = retrofit.create(RedditApi::class.java)

    @Provides
    @Singleton
    fun provideRedditDataRepo(redditDataRepositoryImpl: RedditDataRepositoryImpl)
            : RedditDataRepository = redditDataRepositoryImpl

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
}