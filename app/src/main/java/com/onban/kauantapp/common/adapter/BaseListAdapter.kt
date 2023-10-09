package com.onban.kauantapp.common.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseListAdapter<T, VDB : ViewDataBinding> constructor(
    diffUtil : DiffUtil.ItemCallback<T>,
) : ListAdapter<T, BaseViewHolder<VDB>>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VDB> {
        val binding = createBinding(parent)
        return BaseViewHolder(binding, { b, getItemPosition -> initViewHolder(b, getItemPosition)} )
    }

    protected abstract fun createBinding(parent: ViewGroup): VDB

    protected abstract fun initViewHolder(binding: VDB, getItemPosition: () -> Int)

    override fun onBindViewHolder(holder: BaseViewHolder<VDB>, position: Int) {
        bind(holder, position)
    }

    protected abstract fun bind(holder: BaseViewHolder<VDB>, position: Int)
}

class BaseViewHolder<VDB : ViewDataBinding> constructor(
    val binding: VDB,
    onViewHolderInit: (VDB, () -> Int) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        onViewHolderInit.invoke(binding, { adapterPosition })
    }
}