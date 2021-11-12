package com.osamaaftab.arch.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.osamaaftab.arch.common.TestSchedulerProvider
import com.osamaaftab.arch.data.common.Resource
import com.osamaaftab.arch.data.repository.CatsRepositoryImpl
import com.osamaaftab.arch.data.source.local.LocalDataSourceImp
import com.osamaaftab.arch.data.source.remote.RemoteDataSourceImp
import com.osamaaftab.arch.domain.entity.Cat
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Observer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.*


class CatsRepositoryTest {

    /**
     * Runs Arch Components on a synchronous executor
     */
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var localDataSourceImp: LocalDataSourceImp

    @Mock
    private lateinit var remoteDataSourceImp: RemoteDataSourceImp

    @Captor
    private lateinit var listCaptor: ArgumentCaptor<Resource<List<Cat>>>

    /**
     * Runs RxJava synchronously
     */
    private val schedulerProvider = TestSchedulerProvider()

    /**
     * Receives emissions from [TasksTasksRepositoryImpl.tasksSubject]
     */
    private val tasksObserver: Observer<Resource<List<Cat>>> = mock()

    private lateinit var repository: CatsRepositoryImpl

    @Captor
    private lateinit var resourceDataCaptor: ArgumentCaptor<Resource<List<Cat>>>

    @Before
    fun setup() {
        // Allows us to use @Mock annotations
        MockitoAnnotations.initMocks(this)

        repository = CatsRepositoryImpl.getInstance(
                localDataSourceImp,
                remoteDataSourceImp,
                schedulerProvider
        ).apply {
            subscribe(tasksObserver)
        }
    }

    @After
    fun clearMocks() {
        // Ensures inline Kotlin mocks do not leak
        Mockito.framework().clearInlineMocks()
        CatsRepositoryImpl.destroy()
    }

    @Test
    fun `task subject subscribed`() {
        verify(tasksObserver, times(1)).onSubscribe(any())
    }

    @Test
    fun `task subscription observes task emission`() {
        val resource: Resource<List<Cat>> = mock()
        repository.tasksSubject.onNext(resource)
        verify(tasksObserver, times(1)).onNext(resource)
    }

    @Test
    fun `cache is cleared and marked dirty on refresh`() {
        repository.apply {
            cachedTasks["a"] = mock()
            isCacheDirty = false
        }

        repository.refresh()

        assertTrue(repository.cachedTasks.isEmpty())
        assertTrue(repository.isCacheDirty)
    }

    @Test
    fun `cached tasks returned when cache is not empty or dirty`() {
        val task = Cat("a", "test")
        repository.apply {
            isCacheDirty = false
            cachedTasks["a"] = task
        }
        whenever(localDataSourceImp.getCats(25,1)).thenReturn(Observable.never<Resource<List<Cat>>>())
        val resource: Resource<List<Cat>> = mock()
        whenever(remoteDataSourceImp.getCats(25,1)).thenReturn(Observable.just(resource))

        repository.loadCats(25,1)

        verify(tasksObserver, times(1)).onNext(any())

    }

    @Test
    fun `cached tasks not returned when cache is dirty`() {
        whenever(localDataSourceImp.getCats(25,1)).thenReturn(Observable.never<Resource<List<Cat>>>())
        whenever(remoteDataSourceImp.getCats(25,1)).thenReturn(Observable.never<Resource<List<Cat>>>())

        repository.loadCats(25,1)

        verify(tasksObserver, never()).onNext(any())
    }

    @Test
    fun `remote tasks returned when cache is dirty`() {
        whenever(localDataSourceImp.getCats(25,1)).thenReturn(Observable.never<Resource<List<Cat>>>())
        val resource: Resource<List<Cat>> = mock()
        whenever(remoteDataSourceImp.getCats(25,1)).thenReturn(Observable.just(resource))

        repository.loadCats(25,1)

        verify(tasksObserver, times(1)).onNext(resource)
    }

    @Test
    fun `local tasks returned when cache is dirty and remote call fails`() {
        whenever(remoteDataSourceImp.getCats(25,1)).thenReturn(Observable.error(RuntimeException()))
        val resource: Resource<List<Cat>> = mock()
        whenever(localDataSourceImp.getCats(25,1)).thenReturn(Observable.just(resource))

        repository.loadCats(25,1)

        verify(tasksObserver, times(1)).onNext(resource)
    }

    @Test
    fun `local tasks returned when cache is not dirty but is empty`() {
        val localResource: Resource<List<Cat>> = mock()
        val remoteResource: Resource<List<Cat>> = mock()

        whenever(localDataSourceImp.getCats(25,1)).thenReturn(Observable.just(localResource))
        whenever(remoteDataSourceImp.getCats(25,1)).thenReturn(Observable.just(remoteResource))

        Observable.merge(localDataSourceImp.getCats(25,1), remoteDataSourceImp.getCats(25,1))
        repository.loadCats(25,1)
        verify(tasksObserver, times(1)).onNext(localResource)
        verify(tasksObserver, never()).onError(Throwable())
    }

    @Test
    fun `remote tasks are cached when successfully retrieved`() {
        whenever(localDataSourceImp.getCats(25,1)).thenReturn(Observable.never<Resource<List<Cat>>>())
        val task = mock<Cat> {
            on { id } doReturn "a"
        }
        val data = listOf(task)
        val resource = Resource.Success(data)
        whenever(remoteDataSourceImp.getCats(25,1)).thenReturn(Observable.just(resource))

        repository.loadCats(25,1)

        assertEquals(repository.cachedTasks["a"], task)
    }

    @Test
    fun `remote tasks are saved to db when successfully retrieved`() {
        whenever(localDataSourceImp.getCats(25,1)).thenReturn(Observable.never<Resource<List<Cat>>>())
        val data = listOf(mock<Cat>())
        val resource = Resource.Success(data)
        whenever(remoteDataSourceImp.getCats(25,1)).thenReturn(Observable.just(resource))

        repository.loadCats(25,1)

        verify(localDataSourceImp, times(1)).saveCats(data)
    }

    @Test
    fun `cache is marked clean when remote tasks are cached`() {
        whenever(localDataSourceImp.getCats(25,1)).thenReturn(Observable.never<Resource<List<Cat>>>())
        whenever(localDataSourceImp.saveCats(any())).thenReturn(Completable.never())
        val data = listOf(mock<Cat>())
        val resource = Resource.Success(data)
        whenever(remoteDataSourceImp.getCats(25,1)).thenReturn(Observable.just(resource))

        repository.loadCats(25,1)

        assertFalse(repository.isCacheDirty)
    }

    @Test
    fun `local tasks are cached when successfully retrieved`() {

        repository.isCacheDirty = false
        val task = mock<Cat> {
            on { id } doReturn "a"
        }
        val data = listOf(task)
        val resource = Resource.Success(data)
        whenever(localDataSourceImp.getCats(25,1)).thenReturn(Observable.just(resource))
        whenever(remoteDataSourceImp.getCats(25,1)).thenReturn(Observable.just(resource))

        repository.loadCats(25,1)

        assertEquals(repository.cachedTasks["a"], task)
    }
}
