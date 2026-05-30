package com.arievscalia.kerb.data.datastore

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object KerbPreferences {
    val CURRENT_TRIP_ID = stringPreferencesKey("current_trip_id")
    val WIDGET_LAST_REFRESH = longPreferencesKey("widget_last_refresh")
}
