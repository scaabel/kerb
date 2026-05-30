package com.arievscalia.kerb.data.repository

import com.arievscalia.kerb.data.db.ReminderDao
import com.arievscalia.kerb.data.db.TripDao
import javax.inject.Inject

class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val reminderDao: ReminderDao
) : TripRepository
