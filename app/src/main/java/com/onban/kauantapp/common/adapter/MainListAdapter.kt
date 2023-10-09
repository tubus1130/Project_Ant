package com.onban.kauantapp.common.adapter

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.onban.kauantapp.databinding.MainNewsItemBinding
import com.onban.kauantapp.databinding.MainNewsTimelineBinding
import com.onban.network.data.NewsData
import java.util.*


class MainListAdapter(
    private val onClick: (NewsData) -> Unit
) : BaseListAdapter<NewsData, MainNewsItemBinding>(NewsDataDiffCallback) {

    override fun createBinding(parent: ViewGroup): MainNewsItemBinding {
        val inflater = LayoutInflater.from(parent.context)
        return MainNewsItemBinding.inflate(inflater, parent, false)
    }

    override fun initViewHolder(binding: MainNewsItemBinding, getItemPosition: () -> Int) {
        binding.root.setOnClickListener {
            onClick.invoke(getItem(getItemPosition.invoke()))
        }
    }

    override fun bind(holder: BaseViewHolder<MainNewsItemBinding>, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            newsData = item

            textViewItemDate.visibility = if (isHeader(position)) View.VISIBLE else View.INVISIBLE

            executePendingBindings()
        }
    }

    fun isHeader(position: Int): Boolean {
        val before = position - 1
        if (before < 0) return true
        if (position == itemCount - 1) return false
        val beforeItemDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(getItem(before).date)
        val curItemDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(getItem(position).date)

        return beforeItemDate > curItemDate
    }

    fun getHeaderView(list: RecyclerView, position: Int): View? {
        val item = getItem(position)

        //얘는 그려지지 않은 녀석임
        val binding = MainNewsTimelineBinding.inflate(LayoutInflater.from(list.context), list, false)
        binding.date = item.date
        binding.executePendingBindings()
        return binding.root
    }
}

object NewsDataDiffCallback : DiffUtil.ItemCallback<NewsData>() {

    override fun areItemsTheSame(oldItem: NewsData, newItem: NewsData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: NewsData, newItem: NewsData): Boolean {
        return oldItem.newsNo == newItem.newsNo
    }
}