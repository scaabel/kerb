package com.arievscalia.kerb.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [TripEntity::class, ReminderEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class KerbDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun reminderDao(): ReminderDao
}
