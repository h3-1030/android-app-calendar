package com.h31030.personalcalendar.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.h31030.personalcalendar.R

sealed class Destination(val route: String) {
    data object Calendar : Destination("calendar")
    data object TodoList : Destination("todo_list")
    data object BudgetSummary : Destination("budget_summary")
    data object Settings : Destination("settings")
    data object DayDetail : Destination("day_detail/{date}") {
        fun createRoute(date: String) = "day_detail/$date"
    }
}

data class BottomNavItem(val destination: Destination, val labelRes: Int, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Destination.Calendar, R.string.nav_calendar, Icons.Default.CalendarMonth),
    BottomNavItem(Destination.TodoList, R.string.nav_todo, Icons.Default.CheckBox),
    BottomNavItem(Destination.BudgetSummary, R.string.nav_budget, Icons.Default.AccountBalanceWallet),
    BottomNavItem(Destination.Settings, R.string.nav_settings, Icons.Default.Settings),
)
