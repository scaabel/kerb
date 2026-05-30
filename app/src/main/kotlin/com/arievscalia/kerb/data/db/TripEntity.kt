package com.arievscalia.kerb.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val connectedAt: Long,
    val disconnectedAt: Long? = null,
    val durationMs: Long? = null
)
