package com.arievscalia.kerb.widget

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import com.arievscalia.kerb.MainActivity
import com.arievscalia.kerb.data.datastore.KerbPreferences
import com.arievscalia.kerb.data.db.ReminderEntity
import com.arievscalia.kerb.data.repository.TripRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

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

@androidx.compose.runtime.Composable
private fun WidgetContent(
    isConnected: Boolean,
    reminders: List<ReminderEntity>,
    pendingCount: Int
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .clickable(actionStartActivity<MainActivity>())
            .padding(12)
    ) {
        // Header
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = "Kerb",
                style = TextStyle(fontWeight = androidx.glance.text.FontWeight.Bold)
            )
            Spacer(GlanceModifier.defaultWeight())
            Text(text = if (isConnected) "●" else "○")
        }

        Spacer(GlanceModifier.height(8))

        // Reminder rows
        if (reminders.isEmpty()) {
            Text(
                text = "No pending reminders",
                style = TextStyle(
                    color = ColorProvider(
                        androidx.glance.color.ColorProvider(
                            day = androidx.compose.ui.graphics.Color.Gray,
                            night = androidx.compose.ui.graphics.Color.LightGray
                        )
                    )
                )
            )
        } else {
            reminders.forEach { reminder ->
                Text(
                    text = "· ${reminder.text.take(30)}${if (reminder.text.length > 30) "…" else ""}",
                    maxLines = 1
                )
            }
        }

        Spacer(GlanceModifier.defaultWeight())

        // Footer
        Text(
            text = "$pendingCount pending",
            style = TextStyle(fontSize = androidx.compose.ui.unit.TextUnit(11f, androidx.compose.ui.unit.TextUnitType.Sp))
        )
    }
}
