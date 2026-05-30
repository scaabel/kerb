package com.arievscalia.kerb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.arievscalia.kerb.ui.feature.history.HistoryScreen
import com.arievscalia.kerb.ui.feature.home.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object History : Screen("history")
    object AddReminder : Screen("add_reminder")
}

@Composable
fun KerbNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.History.route) { HistoryScreen(navController) }
    }
}
