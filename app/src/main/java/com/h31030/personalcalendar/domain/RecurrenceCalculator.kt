package com.h31030.personalcalendar.domain

import com.h31030.personalcalendar.data.local.entity.EventEntity
import com.h31030.personalcalendar.data.local.entity.RepeatRule
import java.time.LocalDate

/**
 * 予定の繰り返し（毎週／毎月の単純なパターンのみ、要件定義書 4.1節）が
 * 指定日に「発生するかどうか」を判定する。1回分だけの例外編集・除外は
 * 将来拡張のため、繰り返し予定は常にルールから機械的に算出する。
 */
object RecurrenceCalculator {
    fun occursOn(event: EventEntity, date: LocalDate): Boolean {
        if (date.isBefore(event.date)) return false
        event.repeatUntil?.let { until -> if (date.isAfter(until)) return false }

        return when (event.repeatRule) {
            RepeatRule.NONE -> date == event.date
            RepeatRule.WEEKLY -> date.dayOfWeek == event.date.dayOfWeek
            RepeatRule.MONTHLY -> date.dayOfMonth == event.date.dayOfMonth
        }
    }
}
