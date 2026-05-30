package com.arievscalia.kerb.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.glance.unit.ColorProvider
import com.arievscalia.kerb.MainActivity
import com.arievscalia.kerb.data.datastore.KerbPreferences
import com.arievscalia.kerb.data.db.ReminderEntity
import com.arievscalia.kerb.data.repository.TripRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

@EntryPoint
@InstallIn(SingletonComponent::class)
interface KerbWidgetEntryPoint {
    fun tripRepository(): TripRepository
    fun kerbPreferences(): KerbPreferences
}

class KerbWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            KerbWidgetEntryPoint::class.java
        )
        val repo = entryPoint.tripRepository()
        val prefs = entryPoint.kerbPreferences()

        val tripId = prefs.currentTripId.first()
        val isConnected = tripId != null
        val reminders = if (tripId != null) {
            repo.getRemindersForTripOnce(tripId)
        } else {
            emptyList()
        }

        val pendingReminders = reminders.filter { !it.isComplete }
        val displayReminders = pendingReminders.take(3)

        provideContent {
            GlanceTheme {
                WidgetContent(
                    isConnected = isConnected,
                    reminders = displayReminders,
                    pendingCount = pendingReminders.size
                )
            }
        }
    }
}

@Composable
private fun WidgetContent(
    isConnected: Boolean,
    reminders: List<ReminderEntity>,
    pendingCount: Int
) {
    val mutedColor = ColorProvider(Color(0xFF888888))

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .clickable(actionStartActivity<MainActivity>())
            .padding(12.dp)
    ) {
        // Header
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = "Kerb",
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
            Spacer(GlanceModifier.defaultWeight())
            Text(text = if (isConnected) "●" else "○")
        }

        Spacer(GlanceModifier.height(8.dp))

        // Reminder rows
        if (reminders.isEmpty()) {
            Text(
                text = "No pending reminders",
                style = TextStyle(color = mutedColor)
            )
        } else {
            reminders.forEach { reminder ->
                val truncated = reminder.text.take(30) + if (reminder.text.length > 30) "…" else ""
                Text(text = "· $truncated")
            }
        }

        Spacer(GlanceModifier.defaultWeight())

        // Footer
        Text(
            text = "$pendingCount pending",
            style = TextStyle(color = mutedColor)
        )
    }
}
