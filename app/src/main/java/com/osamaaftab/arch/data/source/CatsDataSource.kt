package com.osamaaftab.arch.data.source

import com.osamaaftab.arch.data.common.Resource
import com.osamaaftab.arch.domain.entity.Cat
import io.reactivex.Observable

/**
 * Main entry point for accessing tasks data.
 */
interface CatsDataSource {

    fun getCats(pageSize : Int, pageNo : Int): Observable<Resource<List<Cat>>>

}