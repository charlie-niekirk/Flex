package me.cniekirk.flex.di

import android.content.Context
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.cniekirk.flex.BuildConfig
import me.cniekirk.flex.data.remote.RedditApi
import me.cniekirk.flex.data.remote.repo.RedditDataRepositoryImpl
import me.cniekirk.flex.domain.RedditDataRepository
import okhttp3.Cache
import okhttp3.OkHttpClient
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
    fun provideRetrofit(okHttpClient: Lazy<OkHttpClient>): Retrofit {
        return Retrofit.Builder()
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(MoshiConverterFactory.create())
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
}