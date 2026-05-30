package com.arievscalia.kerb.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity)

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE tripId = :tripId ORDER BY createdAt ASC")
    fun getByTripId(tripId: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE tripId = :tripId ORDER BY createdAt ASC")
    suspend fun getByTripIdOnce(tripId: String): List<ReminderEntity>

    @Query("UPDATE reminders SET isComplete = :isComplete WHERE id = :id")
    suspend fun setComplete(id: String, isComplete: Boolean)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun delete(id: String)
}
