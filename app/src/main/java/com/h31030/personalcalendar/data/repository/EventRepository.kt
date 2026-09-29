package com.h31030.personalcalendar.data.repository

import com.h31030.personalcalendar.data.local.dao.EventDao
import com.h31030.personalcalendar.data.local.entity.EventEntity
import com.h31030.personalcalendar.domain.RecurrenceCalculator
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventRepository(private val eventDao: EventDao) {
    /** 繰り返し予定を展開した上で、指定日に実際に発生する予定の一覧を返す。 */
    fun observeByDate(date: LocalDate): Flow<List<EventEntity>> =
        eventDao.observeCandidatesForDate(date).map { candidates ->
            candidates.filter { RecurrenceCalculator.occursOn(it, date) }
        }

    /** 繰り返し予定を展開した上で、範囲内で予定が発生する日付の一覧を返す（カレンダーのインジケーター用）。 */
    fun observeDatesInRange(start: LocalDate, end: LocalDate): Flow<List<LocalDate>> =
        eventDao.observeCandidatesInRange(start, end).map { candidates ->
            generateSequence(start) { it.plusDays(1) }
                .takeWhile { !it.isAfter(end) }
                .filter { day -> candidates.any { RecurrenceCalculator.occursOn(it, day) } }
                .toList()
        }

    suspend fun save(event: EventEntity) {
        if (event.id == 0L) eventDao.insert(event) else eventDao.update(event)
    }

    suspend fun delete(event: EventEntity) = eventDao.delete(event)
}
