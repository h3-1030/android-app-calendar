package com.h31030.personalcalendar.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.h31030.personalcalendar.data.local.entity.CategoryEntity
import com.h31030.personalcalendar.data.local.entity.TransactionType
import com.h31030.personalcalendar.data.repository.CategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val categoryRepository: CategoryRepository) : ViewModel() {

    val categories: StateFlow<List<CategoryEntity>> = categoryRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addCategory(name: String, type: TransactionType) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val sortOrder = categories.value.count { it.type == type }
            categoryRepository.save(CategoryEntity(name = name.trim(), type = type, sortOrder = sortOrder))
        }
    }

    fun toggleQuickAccess(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.save(category.copy(isQuickAccess = !category.isQuickAccess))
        }
    }

    fun delete(category: CategoryEntity) {
        viewModelScope.launch { categoryRepository.delete(category) }
    }

    companion object {
        fun factory(categoryRepository: CategoryRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                SettingsViewModel(categoryRepository) as T
        }
    }
}
