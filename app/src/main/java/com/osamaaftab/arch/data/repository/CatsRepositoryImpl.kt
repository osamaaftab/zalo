package com.osamaaftab.arch.data.repository

import androidx.annotation.VisibleForTesting
import com.osamaaftab.arch.common.SchedulerProvider
import com.osamaaftab.arch.common.ToDoSchedulerProvider
import com.osamaaftab.arch.data.common.Resource
import com.osamaaftab.arch.data.source.LocalDataSource
import com.osamaaftab.arch.data.source.RemoteDataSource
import com.osamaaftab.arch.data.source.remote.RemoteDataSourceImp
import com.osamaaftab.arch.domain.entity.Cat
import com.osamaaftab.arch.domain.repository.CatsRepository
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 *
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
class CatsRepositoryImpl constructor(
    private val tasksLocalDataSourceLocalDataSource: LocalDataSource,
    private val tasksRemoteDataSourceRemoteDataSource: RemoteDataSource,
    private val schedulerProvider: SchedulerProvider
) : CatsRepository {

    @VisibleForTesting
    lateinit var tasksSubject: BehaviorSubject<Resource<List<Cat>>>

    /**
     * Map of cached tasks using their id as the key
     */
    @VisibleForTesting
    var cachedTasks: LinkedHashMap<String, Cat> = LinkedHashMap()

    /**
     * When true indicates cached data should not be used
     */
    @VisibleForTesting
    var isCacheDirty = true

    override fun subscribe(observer: Observer<Resource<List<Cat>>>) {
        tasksSubject = BehaviorSubject.create()
        tasksSubject.subscribe(observer)
    }

    override fun loadCats(pageSize: Int, pageNo: Int) {
        Timber.tag(TAG).i("loadTasks: isCacheDirty = $isCacheDirty")


        val observable =
            getAndCacheRemoteCats(pageSize, pageNo).onErrorResumeNext(
                getAndCacheLocalCats(
                    pageSize,
                    pageNo
                )
            )

        observable
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.main)
            .subscribe(tasksSubject)
    }


    override fun refresh() {
        Timber.tag(TAG).i("refresh() called")
        cachedTasks.clear()
        isCacheDirty = true
    }

    private fun getAndCacheRemoteCats(
        pageSize: Int,
        pageNo: Int
    ): Observable<Resource<List<Cat>>> =
        tasksRemoteDataSourceRemoteDataSource.getCats(pageSize, pageNo)
            .doOnNext { resource ->
                Timber.tag(TAG).d("getAndCacheRemoteTasks: emitted $resource")
                resource.data?.let { tasks ->
                    cache(tasks)
                    saveToDb(tasks)
                    isCacheDirty = false
                }
            }

    private fun getAndCacheLocalCats(pageSize: Int, pageNo: Int): Observable<Resource<List<Cat>>> =
        tasksLocalDataSourceLocalDataSource.getCats(pageSize, pageNo)
            .doOnNext { resource ->
                Timber.tag(TAG).d("getAndCacheLocalTasks: emitted $resource")
                resource.data?.let { cache(it) }
            }

    private fun cache(cats: List<Cat>?) {
        Timber.tag(TAG).d("cache: $cats")
        cats?.apply {
            cachedTasks.clear()
            forEach { cache(it) }
        }
    }

    private fun cache(cat: Cat?) {
        Timber.tag(TAG).d("cache: $cat")
        cat?.id?.let { id ->
            cachedTasks[id] = cat
        }
    }

    private fun saveToDb(cats: List<Cat>?) {
        Timber.tag(TAG).d("saveToDb: $cats")
        cats?.let {
            tasksLocalDataSourceLocalDataSource.saveCats(it)
                .subscribeOn(schedulerProvider.io)
                .observeOn(schedulerProvider.main)
                .subscribe()
        }
    }

    companion object {
        private const val TAG = "TasksRepository"

        private var INSTANCE: CatsRepositoryImpl? = null

        fun getInstance(
            tasksLocalDataSourceLocalDataSource: LocalDataSource,
            tasksRemoteDataSourceRemoteDataSource: RemoteDataSource = RemoteDataSourceImp.getInstance(),
            schedulerProvider: SchedulerProvider = ToDoSchedulerProvider()
        ): CatsRepositoryImpl = INSTANCE ?: synchronized(this) {
            INSTANCE ?: CatsRepositoryImpl(
                tasksLocalDataSourceLocalDataSource,
                tasksRemoteDataSourceRemoteDataSource,
                schedulerProvider
            ).also { INSTANCE = it }
        }

        @VisibleForTesting
        fun destroy() {
            INSTANCE = null
        }
    }
}