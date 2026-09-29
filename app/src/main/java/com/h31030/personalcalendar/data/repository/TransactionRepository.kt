package com.h31030.personalcalendar.data.repository

import com.h31030.personalcalendar.data.local.dao.TransactionDao
import com.h31030.personalcalendar.data.local.entity.TransactionEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    fun observeByDate(date: LocalDate): Flow<List<TransactionEntity>> = transactionDao.observeByDate(date)

    fun observeDatesInRange(start: LocalDate, end: LocalDate): Flow<List<LocalDate>> =
        transactionDao.observeDatesInRange(start, end)

    fun observeByDateRange(start: LocalDate, end: LocalDate): Flow<List<TransactionEntity>> =
        transactionDao.observeByDateRange(start, end)

    suspend fun save(transaction: TransactionEntity) {
        if (transaction.id == 0L) transactionDao.insert(transaction) else transactionDao.update(transaction)
    }

    suspend fun delete(transaction: TransactionEntity) = transactionDao.delete(transaction)
}
