package com.h31030.personalcalendar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.h31030.personalcalendar.data.local.entity.EventEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE date = :date ORDER BY startTime IS NULL, startTime")
    fun observeByDate(date: LocalDate): Flow<List<EventEntity>>

    @Query("SELECT DISTINCT date FROM events WHERE date BETWEEN :start AND :end")
    fun observeDatesInRange(start: LocalDate, end: LocalDate): Flow<List<LocalDate>>

    @Insert
    suspend fun insert(event: EventEntity): Long

    @Update
    suspend fun update(event: EventEntity)

    @Delete
    suspend fun delete(event: EventEntity)
}
