package com.arievscalia.kerb.ui.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arievscalia.kerb.data.db.ReminderEntity
import com.arievscalia.kerb.data.db.TripEntity
import com.arievscalia.kerb.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class TripSummary(
    val trip: TripEntity,
    val reminders: List<ReminderEntity>
) {
    val completionRate: Float
        get() = if (reminders.isEmpty()) 0f
        else reminders.count { it.isComplete }.toFloat() / reminders.size
}

data class HistoryUiState(
    val trips: List<TripSummary> = emptyList()
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = tripRepository.getAllTrips()
        .map { trips ->
            val summaries = trips
                .filter { it.disconnectedAt != null } // only completed trips
                .map { trip ->
                    val reminders = tripRepository.getRemindersForTripOnce(trip.id)
                    TripSummary(trip, reminders)
                }
            HistoryUiState(trips = summaries)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryUiState()
        )
}
