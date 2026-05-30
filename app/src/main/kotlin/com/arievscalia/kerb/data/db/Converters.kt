package com.arievscalia.kerb.data.db

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromTrigger(trigger: ReminderTrigger): String = trigger.name

    @TypeConverter
    fun toTrigger(value: String): ReminderTrigger = ReminderTrigger.valueOf(value)
}
