package com.osamaaftab.arch.presentation.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.osamaaftab.arch.data.common.Resource
import com.osamaaftab.arch.domain.entity.Cat
import com.osamaaftab.arch.domain.usecase.ISubscribeCatsListUseCase
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.disposables.Disposable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class CatsViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var useCaseI: ISubscribeCatsListUseCase

    lateinit var viewModel: CatsListViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        viewModel = CatsListViewModel(useCaseI)
    }

    @After
    fun clearMocks() {
        // Ensures inline Kotlin mocks do not leak
        Mockito.framework().clearInlineMocks()
    }

    @Test
    fun `subscribes to use case on init`() {
        verify(useCaseI, times(1)).subscribe(any())
        verify(useCaseI, times(1)).load(25,1)
    }

    @Test
    fun `refreshes use case on refresh`() {
        var disposable: Disposable = mock()
        viewModel.catsObserver.onSubscribe(disposable)
        viewModel.refresh()
        verify(useCaseI, times(1)).refresh()
    }

    @Test
    fun `error event triggered when failure resource observed`() {
        val loadingObserver: Observer<Boolean> = mock()
        viewModel.loadingEvent.observeForever(loadingObserver)
        val errorObserver: Observer<Throwable> = mock()
        viewModel.errorEvent.observeForever(errorObserver)

        val error = mock<Throwable>()
        viewModel.catsObserver.onNext(Resource.Failure(error))

        verify(loadingObserver, times(1)).onChanged(false)
        verify(errorObserver, times(1)).onChanged(error)
    }

    @Test
    fun `cats event triggered when success resource observed`() {
        val loadingObserver: Observer<Boolean> = mock()
        viewModel.loadingEvent.observeForever(loadingObserver)
        val catsObserver: Observer<List<Cat>> = mock()
        viewModel.catList.observeForever(catsObserver)

        val data = mock<List<Cat>>()
        viewModel.catsObserver.onNext(Resource.Success(data))

        verify(loadingObserver, times(1)).onChanged(false)
        verify(catsObserver, times(1)).onChanged(data)
    }

    @Test
    fun `error event triggered when uncaught error thrown`() {
        val loadingObserver: Observer<Boolean> = mock()
        viewModel.loadingEvent.observeForever(loadingObserver)
        val errorObserver: Observer<Throwable> = mock()
        viewModel.errorEvent.observeForever(errorObserver)

        val error = mock<Throwable>()
        viewModel.catsObserver.onError(error)

        verify(loadingObserver, times(1)).onChanged(false)
        verify(errorObserver, times(1)).onChanged(error)
    }

    @Test
    fun `loading event triggered when cats subject completes`() {
        val observer: Observer<Boolean> = mock()
        viewModel.loadingEvent.observeForever(observer)

        viewModel.catsObserver.onComplete()

        verify(observer, times(1)).onChanged(false)
    }
}