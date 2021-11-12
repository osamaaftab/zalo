package com.osamaaftab.arch.data.api

import com.osamaaftab.arch.domain.entity.Cat
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CatsApiService {

    object Config {
        const val BASE_URL = "https://api.thecatapi.com/"
    }

    /**
     * Example of a response that encapsulates results in a JSON payload
     */
    @GET("v1/images/search")
    fun getCats(
        @Query("limit")
        limit: Int, @Query("page")
        page: Int,
        @Query("mime_types")
        mime_types: String
    ): Call<List<Cat>>

    /**
     * Example of a response that contains a JSON object
     */
    @GET("b/{id}/latest")
    fun getTask(@Path("id") id: String): Observable<Cat>
}