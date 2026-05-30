package com.arievscalia.kerb

import android.app.Application
import com.arievscalia.kerb.service.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KerbApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
    }
}
