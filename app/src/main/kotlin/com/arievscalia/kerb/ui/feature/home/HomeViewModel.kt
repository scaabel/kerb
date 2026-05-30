package com.arievscalia.kerb.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arievscalia.kerb.data.datastore.KerbPreferences
import com.arievscalia.kerb.data.db.ReminderEntity
import com.arievscalia.kerb.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isConnected: Boolean = false,
    val connectedAt: Long? = null,
    val reminders: List<ReminderEntity> = emptyList()
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
                flow {
                    val trip = tripRepository.getActiveTrip()
                    val remindersFlow = tripRepository.getRemindersForTrip(tripId)
                        .map { reminders ->
                            HomeUiState(
                                isConnected = true,
                                connectedAt = trip?.connectedAt,
                                reminders = reminders
                            )
                        }
                    emitAll(remindersFlow)
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
}
