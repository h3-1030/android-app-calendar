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
    /**
     * 指定日ちょうどの予定に加え、繰り返し予定（毎週／毎月）は元の日付に関わらず
     * 該当しうるため全件を候補として返す。実際にその日に発生するかどうかは
     * [com.h31030.personalcalendar.domain.RecurrenceCalculator] で判定する。
     */
    @Query("SELECT * FROM events WHERE date = :date OR repeatRule != 'NONE' ORDER BY startTime IS NULL, startTime")
    fun observeCandidatesForDate(date: LocalDate): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE date BETWEEN :start AND :end OR repeatRule != 'NONE'")
    fun observeCandidatesInRange(start: LocalDate, end: LocalDate): Flow<List<EventEntity>>

    @Insert
    suspend fun insert(event: EventEntity): Long

    @Update
    suspend fun update(event: EventEntity)

    @Delete
    suspend fun delete(event: EventEntity)
}
