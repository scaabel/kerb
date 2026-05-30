package com.arievscalia.kerb.ui.feature.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arievscalia.kerb.data.datastore.KerbPreferences
import com.arievscalia.kerb.data.db.ReminderTrigger
import com.arievscalia.kerb.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddReminderViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val kerbPreferences: KerbPreferences
) : ViewModel() {

    fun saveReminder(
        text: String,
        trigger: ReminderTrigger,
        onSuccess: () -> Unit
    ) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val tripId = kerbPreferences.currentTripId.first() ?: return@launch
            tripRepository.addReminder(tripId, text, trigger)
            onSuccess()
        }
    }
}
