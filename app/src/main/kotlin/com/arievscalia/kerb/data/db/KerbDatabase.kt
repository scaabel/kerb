package com.arievscalia.kerb.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TripEntity::class, ReminderEntity::class],
    version = 1,
    exportSchema = true
)
abstract class KerbDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun reminderDao(): ReminderDao
}
