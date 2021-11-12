package com.osamaaftab.arch.data.source.remote

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.osamaaftab.arch.data.api.ApiResponse
import com.osamaaftab.arch.data.api.CatsApiService
import com.osamaaftab.arch.data.common.Resource
import com.osamaaftab.arch.data.source.RemoteDataSource
import com.osamaaftab.arch.domain.entity.Cat
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observer
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatsRemoteDataSourceImpTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var api: CatsApiService

    private lateinit var dataSourceImp: RemoteDataSource

    @Captor
    private lateinit var resourceDataCaptor: ArgumentCaptor<Resource<List<Cat>>>

    @Captor
    private lateinit var catCaptor: ArgumentCaptor<Resource<Cat>>


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        dataSourceImp = RemoteDataSourceImp.getInstance(api)
    }

    @After
    fun clearMocks() {
        // Ensures inline Kotlin mocks do not leak
        Mockito.framework().clearInlineMocks()
        RemoteDataSourceImp.destroy()
    }



    @Test
    fun `api call cancelled when observable is disposed`() {
        val apiCall = mock<Call<List<Cat>>>()
        whenever(api.getCats(25,1,"jpeg")).thenReturn(apiCall)

        val observer = dataSourceImp.getCats(25,1).test()
        observer.dispose()

        verify(apiCall, times(1)).cancel()
    }


    @Test
    fun `error emitted on api call failure`() {
        val apiCall = mock<Call<List<Cat>>>()
        val apiCall1 = mock<Call<ApiResponse<Cat>>>()

        whenever(api.getCats(25,1,"jpeg")).thenReturn(apiCall)

        val exception = RuntimeException()
        whenever(apiCall.enqueue(any())).doAnswer {
            val callback: Callback<ApiResponse<Cat>> = it.getArgument(0)
            callback.onFailure(apiCall1, exception)
            null
        }

        val observer: Observer<Resource<List<Cat>>> = mock()
        dataSourceImp.getCats(25,1).subscribe(observer)

        verify(observer, times(1)).onError(exception)
    }


    @Test
    fun `cats emitted on api response success`() {
        val apiCall = mock<Call<List<Cat>>>()
        val apiCall1 = mock<Call<ApiResponse<Cat>>>()

        whenever(api.getCats(25,1,"jpeg")).thenReturn(apiCall)

        val cats = mock<List<Cat>>()
        whenever(apiCall.enqueue(any())).doAnswer {
            val callback: Callback<ApiResponse<Cat>> = it.getArgument(0)
            callback.onResponse(apiCall1, Response.success(ApiResponse(true, "", cats)))
            null
        }

        val observer: Observer<Resource<List<Cat>>> = mock()
        dataSourceImp.getCats(25,1).subscribe(observer)

        verify(observer, times(2)).onNext(resourceDataCaptor.capture())
        val resource = resourceDataCaptor.allValues.last()
        assertTrue(resource is Resource.Success)
        assertEquals(resource.data, cats)
    }

    @Test
    fun `error emitted on api response failure`() {
        val apiCall = mock<Call<List<Cat>>>()
        val apiCall1 = mock<Call<ApiResponse<Cat>>>()

        whenever(api.getCats(25,1,"png")).thenReturn(apiCall)

        val responseBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "{}")

        whenever(apiCall.enqueue(any())).doAnswer {
            val callback: Callback<ApiResponse<Cat>> = it.getArgument(0)
            callback.onResponse(apiCall1, Response.error(404, responseBody))
            null
        }

        val observer = dataSourceImp.getCats(25,1).test()
        observer.assertError(RuntimeException::class.java)
    }

    @Test
    fun `error emitted from api response error message`() {
        val apiCall = mock<Call<List<Cat>>>()
        val apiCall1 = mock<Call<ApiResponse<Cat>>>()

        whenever(api.getCats(25,1,"png")).thenReturn(apiCall)

        val msg = "Test error message"
        whenever(apiCall.enqueue(any())).doAnswer {
            val callback: Callback<ApiResponse<Cat>> = it.getArgument(0)
            callback.onResponse(apiCall1, Response.success(ApiResponse(false, msg, listOf())))
            null
        }

        val observer = dataSourceImp.getCats(25,1).test()
        observer.assertErrorMessage(msg)
    }
}