package com.h31030.personalcalendar.data.repository

import com.h31030.personalcalendar.data.local.dao.DiaryDao
import com.h31030.personalcalendar.data.local.entity.DiaryEntryEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

class DiaryRepository(private val diaryDao: DiaryDao) {
    fun observeByDate(date: LocalDate): Flow<DiaryEntryEntity?> = diaryDao.observeByDate(date)

    fun observeDatesInRange(start: LocalDate, end: LocalDate): Flow<List<LocalDate>> =
        diaryDao.observeDatesInRange(start, end)

    suspend fun save(entry: DiaryEntryEntity) = diaryDao.upsert(entry)

    suspend fun delete(entry: DiaryEntryEntity) = diaryDao.delete(entry)
}
