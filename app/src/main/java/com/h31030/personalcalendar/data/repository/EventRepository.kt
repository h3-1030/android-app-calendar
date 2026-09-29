package com.h31030.personalcalendar.data.repository

import com.h31030.personalcalendar.data.local.dao.EventDao
import com.h31030.personalcalendar.data.local.entity.EventEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

class EventRepository(private val eventDao: EventDao) {
    fun observeByDate(date: LocalDate): Flow<List<EventEntity>> = eventDao.observeByDate(date)

    fun observeDatesInRange(start: LocalDate, end: LocalDate): Flow<List<LocalDate>> =
        eventDao.observeDatesInRange(start, end)

    suspend fun save(event: EventEntity) {
        if (event.id == 0L) eventDao.insert(event) else eventDao.update(event)
    }

    suspend fun delete(event: EventEntity) = eventDao.delete(event)
}
