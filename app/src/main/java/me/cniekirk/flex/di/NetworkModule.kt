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
import me.cniekirk.flex.data.remote.TwitterApi
import me.cniekirk.flex.data.remote.model.reddit.base.EnvelopeKind
import me.cniekirk.flex.data.remote.model.reddit.envelopes.*
import me.cniekirk.flex.data.remote.model.reddit.util.ForceToBooleanJsonAdapter
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
    @Named("preAuth")
    @Singleton
    fun provideOkHttp(cache: Cache): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(logger)
                .addInterceptor(PreLoginModule.NullRepliesInterceptor)
                .build()
        } else {
            OkHttpClient.Builder()
                .cache(cache)
                .callTimeout(10000, TimeUnit.MILLISECONDS)
                .addInterceptor(PreLoginModule.NullRepliesInterceptor)
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
    @Singleton
    fun provideTwitterApi(@Named("twitter") retrofit: Retrofit): TwitterApi {
        return retrofit.create(TwitterApi::class.java)
    }

}