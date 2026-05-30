package com.arievscalia.kerb.data.repository

import com.arievscalia.kerb.data.db.ReminderEntity
import com.arievscalia.kerb.data.db.ReminderTrigger
import com.arievscalia.kerb.data.db.TripEntity
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    suspend fun startTrip(): TripEntity
    suspend fun endTrip(tripId: String)
    suspend fun getActiveTrip(): TripEntity?
    fun getAllTrips(): Flow<List<TripEntity>>

    suspend fun addReminder(tripId: String, text: String, trigger: ReminderTrigger): ReminderEntity
    fun getRemindersForTrip(tripId: String): Flow<List<ReminderEntity>>
    suspend fun getRemindersForTripOnce(tripId: String): List<ReminderEntity>
    suspend fun setReminderComplete(reminderId: String, isComplete: Boolean)
    suspend fun deleteReminder(reminderId: String)
}
