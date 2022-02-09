package me.cniekirk.flex.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import me.cniekirk.flex.FlexSettings
import me.cniekirk.flex.data.local.prefs.SettingsSerializer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<FlexSettings>
        = DataStoreFactory.create(
            produceFile = { context.dataStoreFile("Settings.pb") },
            serializer = SettingsSerializer
        )
}