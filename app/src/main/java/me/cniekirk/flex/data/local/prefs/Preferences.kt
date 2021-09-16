package me.cniekirk.flex.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class Preferences @Inject constructor(@ApplicationContext private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("keys")

    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val accessTokenFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[ACCESS_TOKEN] ?: ""
        }
    suspend fun setAccessToken(accessToken: String) {
        context.dataStore.edit { keys ->
            keys[ACCESS_TOKEN] = accessToken
        }
    }
}