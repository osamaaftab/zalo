package com.osamaaftab.arch.common

/**
 * Repository provide data from 1 or more data sources and implement an in-memory cache.
 */
interface IRepository {
    /**
     * Clear the cache
     */
    fun refresh()
}