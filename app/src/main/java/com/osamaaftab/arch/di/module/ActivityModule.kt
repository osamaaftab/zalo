package com.osamaaftab.arch.di.module

import com.osamaaftab.arch.presentation.tasks.CatsListViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ActivityModule = module {
    viewModel { CatsListViewModel(get()) }
}