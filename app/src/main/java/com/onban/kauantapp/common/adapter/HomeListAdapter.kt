package com.onban.kauantapp.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.onban.kauantapp.databinding.HomeLogoItemBinding
import com.onban.network.data.CompanyData

class HomeListAdapter(
    val onItemClick: (CompanyData) -> Unit,
) : BaseListAdapter<CompanyData, HomeLogoItemBinding>(CompanyLogoDiffCallback) {

    override fun createBinding(parent: ViewGroup): HomeLogoItemBinding {
        val inflater = LayoutInflater.from(parent.context)
        return HomeLogoItemBinding.inflate(inflater, parent, false)
    }

    override fun initViewHolder(binding: HomeLogoItemBinding, getItemPosition: () -> Int) {
        with(binding) {
            root.setOnClickListener {
                onItemClick.invoke(getItem(getItemPosition()))
            }
        }
    }

    override fun bind(holder: BaseViewHolder<HomeLogoItemBinding>, position: Int) {
        with(holder.binding) {
            companyLogo = getItem(position)
            executePendingBindings()
        }
    }
}

object CompanyLogoDiffCallback : DiffUtil.ItemCallback<CompanyData>() {

    override fun areItemsTheSame(oldItem: CompanyData, newItem: CompanyData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CompanyData, newItem: CompanyData): Boolean {
        return oldItem.name == newItem.name
    }
}