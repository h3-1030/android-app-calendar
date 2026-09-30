package com.h31030.personalcalendar.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.h31030.personalcalendar.ui.budget.BudgetSummaryScreen
import com.h31030.personalcalendar.ui.calendar.CalendarScreen
import com.h31030.personalcalendar.ui.daydetail.DayDetailScreen
import com.h31030.personalcalendar.ui.settings.SettingsScreen
import com.h31030.personalcalendar.ui.todo.TodoListScreen
import java.time.LocalDate

@Composable
fun PersonalCalendarNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomNavItems.any { it.destination.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = backStackEntry?.destination?.hierarchy
                                ?.any { it.route == item.destination.route } == true,
                            onClick = {
                                navController.navigate(item.destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                            label = { Text(stringResource(item.labelRes)) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Calendar.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Destination.Calendar.route) {
                CalendarScreen(
                    onDateClick = { date ->
                        navController.navigate(Destination.DayDetail.createRoute(date.toString()))
                    },
                )
            }
            composable(Destination.TodoList.route) { TodoListScreen() }
            composable(Destination.BudgetSummary.route) { BudgetSummaryScreen() }
            composable(Destination.Settings.route) { SettingsScreen() }
            composable(Destination.DayDetail.route) { entry ->
                val dateArg = entry.arguments?.getString("date") ?: LocalDate.now().toString()
                DayDetailScreen(
                    date = LocalDate.parse(dateArg),
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
