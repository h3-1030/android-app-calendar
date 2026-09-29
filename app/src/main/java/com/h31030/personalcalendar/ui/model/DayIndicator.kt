package com.h31030.personalcalendar.ui.model

import androidx.compose.ui.graphics.Color
import com.h31030.personalcalendar.ui.theme.IndicatorDiary
import com.h31030.personalcalendar.ui.theme.IndicatorEvent
import com.h31030.personalcalendar.ui.theme.IndicatorExpense
import com.h31030.personalcalendar.ui.theme.IndicatorIncome
import com.h31030.personalcalendar.ui.theme.IndicatorTodo

/** カレンダーの日付セルに表示する色分けドット（要件定義書 5節）。 */
enum class DayIndicator(val color: Color) {
    EVENT(IndicatorEvent),
    TODO(IndicatorTodo),
    DIARY(IndicatorDiary),
    EXPENSE(IndicatorExpense),
    INCOME(IndicatorIncome),
}
