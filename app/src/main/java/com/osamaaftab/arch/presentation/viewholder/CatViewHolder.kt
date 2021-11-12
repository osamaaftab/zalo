package com.osamaaftab.arch.presentation.viewholder

import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.osamaaftab.arch.presentation.common.GenericAdapter
import com.osamaaftab.arch.presentation.tasks.CatsListViewModel

class CatViewHolder<T>(
    private val viewDataBinding: ViewDataBinding,
    private val viewModel: CatsListViewModel?
) : RecyclerView.ViewHolder(viewDataBinding.root),
    GenericAdapter.Binder<T> {


    override fun bind(data: T, position: Int) {
        viewDataBinding.setVariable(BR.task, data)
        viewDataBinding.setVariable(BR.position,position)
        viewDataBinding.setVariable(BR.viewModel, viewModel)
        viewDataBinding.executePendingBindings()
    }

    override fun bindList(data: List<T>) {
        viewDataBinding.setVariable(BR.list, data)
    }
}