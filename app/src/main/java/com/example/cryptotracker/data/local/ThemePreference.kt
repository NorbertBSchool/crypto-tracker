package com.example.cryptotracker.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode { SYSTEM, LIGHT, DARK }

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
private val THEME_KEY = stringPreferencesKey("theme_mode")

object ThemePreference {
    fun getThemeMode(context: Context): Flow<ThemeMode> =
        context.dataStore.data.map { prefs ->
            prefs[THEME_KEY]?.let { ThemeMode.valueOf(it) } ?: ThemeMode.SYSTEM
        }

    suspend fun setThemeMode(context: Context, mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = mode.name
        }
    }
}
