package com.osamaaftab.arch.data.source

import com.osamaaftab.arch.domain.entity.Cat
import io.reactivex.Completable

interface LocalDataSource : CatsDataSource{

    fun saveCats(cats: List<Cat>): Completable

    fun deleteCats(catsId: String): Completable
}