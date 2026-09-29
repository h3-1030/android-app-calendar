package com.h31030.personalcalendar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.h31030.personalcalendar.data.local.entity.TransactionEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE date = :date ORDER BY id DESC")
    fun observeByDate(date: LocalDate): Flow<List<TransactionEntity>>

    @Query("SELECT DISTINCT date FROM transactions WHERE date BETWEEN :start AND :end")
    fun observeDatesInRange(start: LocalDate, end: LocalDate): Flow<List<LocalDate>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date, id")
    fun observeByDateRange(start: LocalDate, end: LocalDate): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)
}
