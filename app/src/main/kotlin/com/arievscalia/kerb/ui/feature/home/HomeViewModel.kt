package com.arievscalia.kerb.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arievscalia.kerb.data.datastore.KerbPreferences
import com.arievscalia.kerb.data.db.ReminderEntity
import com.arievscalia.kerb.data.db.TripEntity
import com.arievscalia.kerb.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isConnected: Boolean = false,
    val activeTrip: TripEntity? = null,
    val reminders: List<ReminderEntity> = emptyList(),
    val showAddReminder: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val kerbPreferences: KerbPreferences
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = kerbPreferences.currentTripId
        .flatMapLatest { tripId ->
            if (tripId == null) {
                flowOf(HomeUiState(isConnected = false))
            } else {
                combine(
                    tripRepository.getRemindersForTrip(tripId)
                ) { remindersArray ->
                    val reminders = remindersArray[0]
                    HomeUiState(
                        isConnected = true,
                        reminders = reminders
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    fun setReminderComplete(reminderId: String, isComplete: Boolean) {
        viewModelScope.launch {
            tripRepository.setReminderComplete(reminderId, isComplete)
        }
    }

    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            tripRepository.deleteReminder(reminderId)
        }
    }

    fun showAddReminder() {
        // Trigger handled via UI state in the screen directly
    }
}
