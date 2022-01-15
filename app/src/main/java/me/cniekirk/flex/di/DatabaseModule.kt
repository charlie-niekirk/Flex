package me.cniekirk.flex.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.cniekirk.flex.data.local.db.AppDatabase
import me.cniekirk.flex.data.local.db.dao.PreLoginUserDao
import me.cniekirk.flex.data.local.db.dao.UserDao
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

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
    fun providePreLoginUserDao(appDatabase: AppDatabase): PreLoginUserDao = appDatabase.preLoginUserDao()

}