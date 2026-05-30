package com.arievscalia.kerb.data.repository

import com.arievscalia.kerb.data.db.ReminderDao
import com.arievscalia.kerb.data.db.ReminderEntity
import com.arievscalia.kerb.data.db.ReminderTrigger
import com.arievscalia.kerb.data.db.TripDao
import com.arievscalia.kerb.data.db.TripEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val reminderDao: ReminderDao
) : TripRepository {

    override suspend fun startTrip(): TripEntity {
        val trip = TripEntity(
            id = UUID.randomUUID().toString(),
            connectedAt = System.currentTimeMillis()
        )
        tripDao.insert(trip)
        return trip
    }

    override suspend fun endTrip(tripId: String) {
        val trip = tripDao.getById(tripId) ?: return
        val disconnectedAt = System.currentTimeMillis()
        tripDao.update(
            trip.copy(
                disconnectedAt = disconnectedAt,
                durationMs = disconnectedAt - trip.connectedAt
            )
        )
    }

    override suspend fun getActiveTrip(): TripEntity? = tripDao.getActive()

    override fun getAllTrips(): Flow<List<TripEntity>> = tripDao.getAll()

    override suspend fun addReminder(
        tripId: String,
        text: String,
        trigger: ReminderTrigger
    ): ReminderEntity {
        val reminder = ReminderEntity(
            id = UUID.randomUUID().toString(),
            tripId = tripId,
            text = text.take(200),
            trigger = trigger,
            createdAt = System.currentTimeMillis()
        )
        reminderDao.insert(reminder)
        return reminder
    }

    override fun getRemindersForTrip(tripId: String): Flow<List<ReminderEntity>> =
        reminderDao.getByTripId(tripId)

    override suspend fun getRemindersForTripOnce(tripId: String): List<ReminderEntity> =
        reminderDao.getByTripIdOnce(tripId)

    override suspend fun setReminderComplete(reminderId: String, isComplete: Boolean) =
        reminderDao.setComplete(reminderId, isComplete)

    override suspend fun deleteReminder(reminderId: String) =
        reminderDao.delete(reminderId)
}
