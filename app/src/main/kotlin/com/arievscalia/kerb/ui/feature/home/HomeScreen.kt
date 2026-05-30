package com.arievscalia.kerb.ui.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.arievscalia.kerb.data.db.ReminderEntity
import com.arievscalia.kerb.ui.feature.reminder.AddReminderSheet
import com.arievscalia.kerb.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddReminder by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kerb") })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {},
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.History.route) },
                    icon = {},
                    label = { Text("History") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            // Connection status pill
            ConnectionStatusPill(isConnected = uiState.isConnected)

            Spacer(Modifier.height(20.dp))

            Text("This trip", style = MaterialTheme.typography.titleSmall)

            Spacer(Modifier.height(8.dp))

            // Reminder list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.reminders, key = { it.id }) { reminder ->
                    SwipeToDeleteReminderItem(
                        reminder = reminder,
                        onCheckedChange = { viewModel.setReminderComplete(reminder.id, it) },
                        onDelete = { viewModel.deleteReminder(reminder.id) }
                    )
                }

                item {
                    // Add reminder dashed button
                    OutlinedCard(
                        onClick = { showAddReminder = true },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "  Add reminder",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Session card
            if (uiState.isConnected && uiState.activeTrip != null) {
                Spacer(Modifier.height(12.dp))
                SessionCard(connectedAt = uiState.activeTrip!!.connectedAt)
            }

            Spacer(Modifier.height(12.dp))
        }
    }

    if (showAddReminder) {
        AddReminderSheet(onDismiss = { showAddReminder = false })
    }
}

@Composable
private fun ConnectionStatusPill(isConnected: Boolean) {
    val label = if (isConnected) "● Connected" else "○ Disconnected"
    val containerColor = if (isConnected)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isConnected)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        shape = RoundedCornerShape(50),
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteReminderItem(
    reminder: ReminderEntity,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = reminder.isComplete,
                    onCheckedChange = onCheckedChange
                )
                Text(
                    text = reminder.text,
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = if (reminder.isComplete) TextDecoration.LineThrough else null,
                    color = if (reminder.isComplete)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SessionCard(connectedAt: Long) {
    val durationMs = System.currentTimeMillis() - connectedAt
    val minutes = (durationMs / 60_000).toInt()
    val seconds = ((durationMs % 60_000) / 1_000).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Current session", style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${minutes}m ${seconds}s",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
