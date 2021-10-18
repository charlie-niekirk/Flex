package me.cniekirk.flex.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class Preferences @Inject constructor(@ApplicationContext private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

    private val downloadDirectory = stringPreferencesKey("media_download_directory")
    private val blurNsfwMedia = booleanPreferencesKey("blur_nsfw")

    val downloadDirFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[downloadDirectory] ?: ""
        }
    val blurNsfwFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[blurNsfwMedia] ?: true
        }

    suspend fun setDownloadDirectory(downloadDir: String) {
        context.dataStore.edit { keys ->
            keys[downloadDirectory] = downloadDir
        }
    }

    suspend fun setShouldBlurNsfw(blurNsfw: Boolean) {
        context.dataStore.edit { keys ->
            keys[blurNsfwMedia] = blurNsfw
        }
    }

}