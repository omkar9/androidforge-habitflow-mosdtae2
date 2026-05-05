package com.androidforge.habitflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.androidforge.habitflow.presentation.ui.habits.HabitListScreen
import com.androidforge.habitflow.presentation.ui.addedit.AddEditHabitScreen
import com.androidforge.habitflow.presentation.ui.detail.HabitDetailScreen
import com.androidforge.habitflow.presentation.ui.settings.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HabitFlowNavGraph()
                }
            }
        }
    }
}

@Composable
fun HabitFlowNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "habit_list"
    ) {
        composable("habit_list") {
            HabitListScreen(
                onNavigateToAddHabit = { navController.navigate("add_edit_habit") },
                onNavigateToHabitDetail = { habitId ->
                    navController.navigate("habit_detail/$habitId")
                },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("add_edit_habit") {
            AddEditHabitScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "habit_detail/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
        ) {
            HabitDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { habitId ->
                    navController.navigate("add_edit_habit?habitId=$habitId")
                }
            )
        }
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
