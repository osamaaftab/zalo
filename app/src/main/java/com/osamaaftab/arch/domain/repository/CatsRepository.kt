package com.osamaaftab.arch.domain.repository

import com.osamaaftab.arch.common.IRepository
import com.osamaaftab.arch.data.common.Resource
import com.osamaaftab.arch.domain.entity.Cat
import io.reactivex.Observer

interface CatsRepository : IRepository {

    /**
     * Observe emissions of the entire task collection
     */
    fun subscribe(observer: Observer<Resource<List<Cat>>>)

    /**
     * Trigger an update from the data sources. Observers added with [subscribe] will receive
     * emissions when the backing data changes.
     *
     * @see [subscribe]
     */
    fun loadCats(pageSize: Int, pageNo: Int)


}