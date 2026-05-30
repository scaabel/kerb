package com.arievscalia.kerb.ui.feature.reminder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arievscalia.kerb.data.db.ReminderTrigger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderSheet(
    onDismiss: () -> Unit,
    viewModel: AddReminderViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var text by remember { mutableStateOf("") }
    var trigger by remember { mutableStateOf(ReminderTrigger.LEAVING) }
    val focusRequester = remember { FocusRequester() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .imePadding()
        ) {
            Text("Add reminder", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text(
                "For this trip only",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { if (it.length <= 200) text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onGloballyPositioned { focusRequester.requestFocus() },
                placeholder = { Text("What do you need to remember?") },
                singleLine = false,
                maxLines = 4,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    viewModel.saveReminder(text, trigger, onDismiss)
                })
            )

            Spacer(Modifier.height(16.dp))

            // Trigger chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReminderTrigger.entries.forEach { option ->
                    FilterChip(
                        selected = trigger == option,
                        onClick = { trigger = option },
                        label = {
                            Text(
                                when (option) {
                                    ReminderTrigger.LEAVING -> "Leaving car"
                                    ReminderTrigger.ENTERING -> "Entering car"
                                    ReminderTrigger.BOTH -> "Both"
                                }
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.saveReminder(text, trigger, onDismiss) },
                modifier = Modifier.fillMaxWidth(),
                enabled = text.isNotBlank()
            ) {
                Text("Save reminder")
            }
        }
    }
}
