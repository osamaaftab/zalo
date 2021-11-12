package com.osamaaftab.arch.data.source.local

import androidx.annotation.VisibleForTesting
import com.osamaaftab.arch.data.common.Resource
import com.osamaaftab.arch.data.source.LocalDataSource
import com.osamaaftab.arch.domain.entity.Cat
import io.reactivex.Completable
import io.reactivex.Observable

class LocalDataSourceImp  constructor(
    private val catsDao: CatsDao
) : LocalDataSource {

    override fun getCats(pageSize : Int, pageNo : Int): Observable<Resource<List<Cat>>> = catsDao.getCats()
        .flatMap { Observable.just(Resource.Success(it.take(pageSize))) }

    override fun saveCats(cats: List<Cat>) = Completable.fromAction {
        catsDao.insertCats(cats)
    }

    override fun deleteCats(catId: String): Completable = catsDao.deleteCatById(catId)

    companion object {
        private var INSTANCE: LocalDataSource? = null

        fun getInstance(
            catsDao: CatsDao
        ): LocalDataSource = INSTANCE ?: synchronized(this) {
            INSTANCE ?: LocalDataSourceImp(catsDao).also { INSTANCE = it }
        }

        @VisibleForTesting
        fun destroy() {
            INSTANCE = null
        }
    }
}