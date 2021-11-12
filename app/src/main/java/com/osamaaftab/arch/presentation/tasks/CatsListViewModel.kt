package com.osamaaftab.arch.presentation.tasks

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import com.osamaaftab.arch.data.common.Resource
import com.osamaaftab.arch.data.common.Resource.Failure
import com.osamaaftab.arch.data.common.Resource.Success
import com.osamaaftab.arch.domain.entity.Cat
import com.osamaaftab.arch.domain.usecase.ISubscribeCatsListUseCase
import com.osamaaftab.arch.presentation.common.BaseViewModel
import com.osamaaftab.arch.util.SingleLiveEvent
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import timber.log.Timber

class CatsListViewModel(
    private val ISubscribeCatsListUseCase: ISubscribeCatsListUseCase
) : BaseViewModel() {

    val loadingEvent = SingleLiveEvent<Boolean>()
    val errorEvent = SingleLiveEvent<Throwable>()

    val catList = MutableLiveData<List<Cat>>()

    @VisibleForTesting
    val catsObserver = object : Observer<Resource<List<Cat>>> {
        override fun onSubscribe(d: Disposable) {
            disposables.add(d)
        }

        override fun onNext(resource: Resource<List<Cat>>) {
            Timber.tag(TAG).d("CatsObserver onNext: resource = $resource")
            when (resource) {
                is Failure -> {
                    errorEvent.value = resource.error
                }
                is Success -> {
                    loadingEvent.value = false
                    catList.value = resource.data
                }
            }
        }

        override fun onError(e: Throwable) {
            // Uncaught errors will land here
            loadingEvent.value = false
            errorEvent.value = e
        }

        override fun onComplete() {
            loadingEvent.value = false
        }
    }


    fun onReLoad(pageSize: Int, pageNo: Int) {
        ISubscribeCatsListUseCase.load(pageSize, pageNo)
    }

    init {
        loadingEvent.value = true
        ISubscribeCatsListUseCase.subscribe(catsObserver)
        ISubscribeCatsListUseCase.load(25, 1)
    }

    fun refresh() {
        ISubscribeCatsListUseCase.refresh()
    }

    companion object {
        private const val TAG = "CatsViewModel"
    }
}