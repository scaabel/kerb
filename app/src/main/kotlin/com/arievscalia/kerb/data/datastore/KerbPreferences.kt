package com.arievscalia.kerb.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kerb_prefs")

@Singleton
class KerbPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val CURRENT_TRIP_ID = stringPreferencesKey("current_trip_id")
        val WIDGET_LAST_REFRESH = longPreferencesKey("widget_last_refresh")
    }

    val currentTripId: Flow<String?> = context.dataStore.data
        .map { it[Keys.CURRENT_TRIP_ID] }

    suspend fun setCurrentTripId(id: String?) {
        context.dataStore.edit { prefs ->
            if (id == null) prefs.remove(Keys.CURRENT_TRIP_ID)
            else prefs[Keys.CURRENT_TRIP_ID] = id
        }
    }

    suspend fun setWidgetLastRefresh(timestamp: Long) {
        context.dataStore.edit { it[Keys.WIDGET_LAST_REFRESH] = timestamp }
    }
}
