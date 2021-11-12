package com.osamaaftab.arch.presentation.common

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.osamaaftab.arch.domain.repository.CatsRepository
import com.osamaaftab.arch.domain.usecase.SubscribeCatsList
import com.osamaaftab.arch.presentation.tasks.CatsListViewModel

class ViewModelFactory private constructor(
    private val CatsRepository: CatsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = with(modelClass) {
        when {
            isAssignableFrom(CatsListViewModel::class.java) -> {
                CatsListViewModel(SubscribeCatsList(CatsRepository))
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(CatsRepository: CatsRepository) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(CatsRepository).also { INSTANCE = it }
            }

        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}