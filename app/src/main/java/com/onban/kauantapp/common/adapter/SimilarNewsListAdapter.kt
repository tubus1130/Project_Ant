package com.onban.kauantapp.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.onban.kauantapp.data.SimilarNewsModel
import com.onban.kauantapp.databinding.AnalysisVp2ItemBinding

class SimilarNewsListAdapter(
    private val onClick: (SimilarNewsModel) -> Unit,
) : BaseListAdapter<SimilarNewsModel, AnalysisVp2ItemBinding>(SimilarNewsDiffCallback) {
    override fun createBinding(parent: ViewGroup): AnalysisVp2ItemBinding {
        val inflater = LayoutInflater.from(parent.context)
        return AnalysisVp2ItemBinding.inflate(inflater, parent, false)
    }

    override fun initViewHolder(binding: AnalysisVp2ItemBinding, getItemPosition: () -> Int) {
        binding.root.setOnClickListener {
            onClick.invoke(getItem(getItemPosition()))
        }
    }

    override fun bind(holder: BaseViewHolder<AnalysisVp2ItemBinding>, position: Int) {
        with(holder.binding) {
            similarNewsModel = getItem(position)
            executePendingBindings()
        }
    }
}

object SimilarNewsDiffCallback : DiffUtil.ItemCallback<SimilarNewsModel>() {

    override fun areItemsTheSame(oldItem: SimilarNewsModel, newItem: SimilarNewsModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: SimilarNewsModel, newItem: SimilarNewsModel): Boolean {
        return oldItem.title == newItem.title
    }
}