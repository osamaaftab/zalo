package com.osamaaftab.arch.data.source.remote

import androidx.annotation.VisibleForTesting
import com.osamaaftab.arch.data.api.CatsApi
import com.osamaaftab.arch.data.api.CatsApiService
import com.osamaaftab.arch.data.common.Resource
import com.osamaaftab.arch.data.source.RemoteDataSource
import com.osamaaftab.arch.domain.entity.Cat
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteDataSourceImp constructor(
    private val api: CatsApiService
) : RemoteDataSource {

    override fun getCats(pageSize : Int, pageNo : Int): Observable<Resource<List<Cat>>> = Observable.create { emitter ->
        // First notify observers that loading has begun
        emitter.onNext(Resource.Loading())

        val apiCall = api.getCats(pageSize, pageNo, "jpeg")

        // Cancel the network call if disposed before it's finished
        emitter.setCancellable { apiCall.cancel() }

        // Queue the API network call
        apiCall.enqueue(object : Callback<List<Cat>> {
            override fun onFailure(call: Call<List<Cat>?>, t: Throwable) {
                emitter.tryOnError(t)
            }

            override fun onResponse(
                call: Call<List<Cat>?>,
                response: Response<List<Cat>?>
            ) {
                if (response.isSuccessful) {
                    val catResponse: List<Cat>? = response.body()
                    emitter.onNext(Resource.Success(catResponse))
                } else {
                    emitter.tryOnError(RuntimeException("Unknown API error"))
                }
            }
        })
    }


    companion object {
        private var INSTANCE: RemoteDataSource? = null

        fun getInstance(
            api: CatsApiService = CatsApi.getInstance().service
        ): RemoteDataSource = INSTANCE ?: synchronized(this) {
            INSTANCE ?: RemoteDataSourceImp(api).also { INSTANCE = it }
        }

        @VisibleForTesting
        fun destroy() {
            INSTANCE = null
        }
    }
}