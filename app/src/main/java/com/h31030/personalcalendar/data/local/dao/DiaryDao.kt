package com.h31030.personalcalendar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.h31030.personalcalendar.data.local.entity.DiaryEntryEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_entries WHERE date = :date LIMIT 1")
    fun observeByDate(date: LocalDate): Flow<DiaryEntryEntity?>

    @Query("SELECT DISTINCT date FROM diary_entries WHERE date BETWEEN :start AND :end")
    fun observeDatesInRange(start: LocalDate, end: LocalDate): Flow<List<LocalDate>>

    /** date にユニーク制約があるため、同日への保存は上書き（追記・編集）として扱う。 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: DiaryEntryEntity): Long

    @Delete
    suspend fun delete(entry: DiaryEntryEntity)
}
