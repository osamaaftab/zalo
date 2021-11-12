package com.osamaaftab.arch.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.osamaaftab.arch.data.common.Resource
import com.osamaaftab.arch.data.source.LocalDataSource
import com.osamaaftab.arch.domain.entity.Cat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.Observer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class CatsLocalDataSourceImpTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var dao: CatsDao

    @Captor
    private lateinit var listCaptor: ArgumentCaptor<Resource<List<Cat>>>

    @Captor
    private lateinit var catCaptor: ArgumentCaptor<Resource<Cat>>

    private val dataSourceImp: LocalDataSource by lazy { LocalDataSourceImp.getInstance(dao) }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @After
    fun clearMocks() {
        // Ensures inline Kotlin mocks do not leak
        Mockito.framework().clearInlineMocks()
        LocalDataSourceImp.destroy()
    }

    @Test
    fun `get all cats`() {
        val cats = mock<List<Cat>>()
        whenever(dao.getCats()).thenReturn(Observable.just(cats))

        val observer: Observer<Resource<List<Cat>>> = mock()
        dataSourceImp.getCats(25,1).subscribe(observer)

        verify(observer, times(1)).onNext(listCaptor.capture())
        assertTrue(listCaptor.value is Resource.Success)
        assertEquals(listCaptor.value.data, cats)
    }


}