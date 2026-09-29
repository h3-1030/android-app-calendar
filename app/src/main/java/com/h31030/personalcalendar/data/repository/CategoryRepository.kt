package com.h31030.personalcalendar.data.repository

import com.h31030.personalcalendar.data.local.dao.CategoryDao
import com.h31030.personalcalendar.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {
    fun observeAll(): Flow<List<CategoryEntity>> = categoryDao.observeAll()

    fun observeQuickAccess(): Flow<List<CategoryEntity>> = categoryDao.observeQuickAccess()

    suspend fun save(category: CategoryEntity) {
        if (category.id == 0L) categoryDao.insert(category) else categoryDao.update(category)
    }

    suspend fun delete(category: CategoryEntity) = categoryDao.delete(category)
}
