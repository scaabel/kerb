package com.arievscalia.kerb.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class ReminderEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val text: String,
    val trigger: ReminderTrigger,
    val isComplete: Boolean = false,
    val createdAt: Long
)
