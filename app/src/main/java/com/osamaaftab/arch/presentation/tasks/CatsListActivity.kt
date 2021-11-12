package com.osamaaftab.arch.presentation.tasks

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Filter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghajari.zoomhelper.ZoomHelper
import com.osamaaftab.arch.R
import com.osamaaftab.arch.common.PaginationListener
import com.osamaaftab.arch.common.PaginationListener.Companion.PAGE_START
import com.osamaaftab.arch.databinding.ActivityListCatsBinding
import com.osamaaftab.arch.domain.entity.Cat
import com.osamaaftab.arch.presentation.common.GenericAdapter
import com.osamaaftab.arch.presentation.viewholder.CatViewHolder
import org.koin.android.viewmodel.ext.android.viewModel


class CatsListActivity : AppCompatActivity() {

    private val viewModel: CatsListViewModel by viewModel()
    lateinit var activityListCatsBinding: ActivityListCatsBinding
    private var adapter: GenericAdapter<Cat>? = null
    private var currentPage: Int = PAGE_START
    private var totalPage = 25
    private var isLoading = false
    var tempList: MutableList<Cat> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityListCatsBinding = DataBindingUtil.setContentView(this, R.layout.activity_list_cats)
        activityListCatsBinding.lifecycleOwner = this
        initAdapter()
        initViewModelObservers()
    }

    private fun initViewModelObservers() {
        viewModel.apply {
            loadingEvent.observe(this@CatsListActivity, Observer { isRefreshing ->
                Log.d(TAG, "loadingEvent observed")
                if(isRefreshing){
                    activityListCatsBinding.progress.visibility = VISIBLE

                }else{
                    activityListCatsBinding.progress.visibility = GONE
                }
            })

            catList.observe(this@CatsListActivity, Observer { catList ->
                Log.d(TAG, "catList observed: $catList")
                if (catList.isEmpty()) {
                    activityListCatsBinding.statusLbl.visibility = VISIBLE
                } else {
                    activityListCatsBinding.statusLbl.visibility = GONE
                }
                tempList.addAll(catList)
                adapter!!.itemList = tempList
                isLoading = false
            })
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return ZoomHelper.getInstance().dispatchTouchEvent(ev!!, this) || super.dispatchTouchEvent(
            ev
        )
    }


    private fun initAdapter() {
        adapter = object : GenericAdapter<Cat>() {

            override fun getViewHolder(viewDataBinding: ViewDataBinding): RecyclerView.ViewHolder {
                return CatViewHolder<Cat>(viewDataBinding, viewModel)
            }

            override fun getLayoutId(): Int {
                return R.layout.item_cat
            }

            override fun getFilter(): Filter {
                TODO("Not yet implemented")
            }
        }
        activityListCatsBinding.newsRecyclerView.layoutManager = GridLayoutManager(this, 3)
        activityListCatsBinding.newsRecyclerView.itemAnimator = DefaultItemAnimator()
        activityListCatsBinding.newsRecyclerView.setHasFixedSize(true)
        activityListCatsBinding.newsRecyclerView.adapter = adapter
        activityListCatsBinding.newsRecyclerView.addOnScrollListener(object :
            PaginationListener(activityListCatsBinding.newsRecyclerView.layoutManager as GridLayoutManager) {
            override fun loadMoreItems() {
                Toast.makeText(this@CatsListActivity, "Loading More Items", Toast.LENGTH_SHORT)
                    .show()
                this@CatsListActivity.isLoading = true;
                this@CatsListActivity.currentPage++;
                viewModel.onReLoad(totalPage, currentPage)
            }

            override val isLoading: Boolean
                get() = this@CatsListActivity.isLoading

        })
    }

    companion object {
        private const val TAG = "CatListActivity"
    }
}