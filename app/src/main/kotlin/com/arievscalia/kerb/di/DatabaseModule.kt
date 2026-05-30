package com.arievscalia.kerb.di

import android.content.Context
import androidx.room.Room
import com.arievscalia.kerb.data.db.KerbDatabase
import com.arievscalia.kerb.data.db.ReminderDao
import com.arievscalia.kerb.data.db.TripDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KerbDatabase =
        Room.databaseBuilder(context, KerbDatabase::class.java, "kerb.db").build()

    @Provides
    fun provideTripDao(db: KerbDatabase): TripDao = db.tripDao()

    @Provides
    fun provideReminderDao(db: KerbDatabase): ReminderDao = db.reminderDao()
}
