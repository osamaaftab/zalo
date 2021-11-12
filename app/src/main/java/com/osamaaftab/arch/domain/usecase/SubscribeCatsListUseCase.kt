package com.osamaaftab.arch.domain.usecase

import com.osamaaftab.arch.data.common.Resource
import com.osamaaftab.arch.domain.entity.Cat
import com.osamaaftab.arch.domain.repository.CatsRepository
import io.reactivex.Observer

/**
 * Due to the simplicity of this example, this use case is somewhat redundant. Typically you'd
 * combine data from multiple repositories and transform them for the View Model in a Use Case
 */
interface ISubscribeCatsListUseCase {
    /**
     * Subscribes the [observer] to emissions of the full task list.
     */
    fun subscribe(observer: Observer<Resource<List<Cat>>>)

    /**
     * Triggers a repository load that may emit cached results
     */
    fun load(pageSize : Int,pageNo : Int)

    /**
     * Triggers a repository load that emits only fresh results; no cache
     */
    fun refresh()
}

class SubscribeCatsList(private val repositoryI: CatsRepository) : ISubscribeCatsListUseCase {

    override fun subscribe(observer: Observer<Resource<List<Cat>>>) = repositoryI.subscribe(observer)

    override fun load(pageSize : Int,pageNo : Int) = repositoryI.loadCats(pageSize,pageNo)

    override fun refresh() = repositoryI.run {
        refresh()
        loadCats(25,1)
    }
}