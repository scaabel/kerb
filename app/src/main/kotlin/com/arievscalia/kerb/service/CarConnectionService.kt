package com.arievscalia.kerb.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class CarConnectionService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}
