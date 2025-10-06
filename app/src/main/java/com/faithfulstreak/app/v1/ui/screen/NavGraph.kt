package com.faithfulstreak.app.v1.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.faithfulstreak.app.v1.viewmodel.StreakViewModel

@Composable
fun AppNavHost(nav: NavHostController, vm: StreakViewModel) {
    NavHost(navController = nav, startDestination = "home") {
        composable("home") { HomeScreen(nav, vm) }
        composable("history") { HistoryScreen(nav) }
    }
}
