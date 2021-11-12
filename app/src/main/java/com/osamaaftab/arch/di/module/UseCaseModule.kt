package com.osamaaftab.arch.di.module

import com.osamaaftab.arch.domain.repository.CatsRepository
import com.osamaaftab.arch.domain.usecase.SubscribeCatsList
import com.osamaaftab.arch.domain.usecase.ISubscribeCatsListUseCase
import org.koin.dsl.module

val UseCaseModule = module {
    single { provideSubscribeCatsListUseCase(get()) }
}


fun provideSubscribeCatsListUseCase(CatsRepository: CatsRepository): ISubscribeCatsListUseCase {
    return SubscribeCatsList(CatsRepository)
}