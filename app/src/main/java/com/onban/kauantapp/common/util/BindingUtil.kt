package com.onban.kauantapp.common.util

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.onban.kauantapp.data.StockItem
import com.onban.kauantapp.view.custom.CircleProgressView
import com.onban.kauantapp.view.custom.StockGraphView
import com.onban.network.data.CompanyData

@BindingAdapter("submitList")
fun <T, VH : RecyclerView.ViewHolder> RecyclerView.submitList(list: List<T>?) {
    list?.let {
        (adapter as ListAdapter<T, VH>).submitList(list)
    }
}

@BindingAdapter(value = ["list", "callback"], requireAll = true)
fun <T, VH : RecyclerView.ViewHolder> RecyclerView.submitList(list: List<T>?, callback: Runnable) {
    list?.let {
        (adapter as ListAdapter<T, VH>).submitList(list, callback)
    }
}

@BindingAdapter(value = ["list", "callback"], requireAll = true)
fun <T, VH : RecyclerView.ViewHolder> ViewPager2.submitList(list: List<T>?, callback: Runnable) {
    list?.let {
        (adapter as ListAdapter<T, VH>).submitList(list, callback)
    }
}

@BindingAdapter("textFromDate")
fun TextView.setTextFromDate(date: String) {
    val tokens = date.split("T")[0].split("-")
    text = tokens[1] + "-" + tokens[2]
}

@BindingAdapter("normalTextFromCompany")
fun TextView.setNormalTextFromCompany(companyData: CompanyData) {
    this.text = companyData.logo
    this.setTextColor(Color.parseColor(companyData.textColor))
}

@BindingAdapter("randomSizeFromCompany")
fun ConstraintLayout.setRandomSizeFromCompany(companyData: CompanyData) {
    val back = GradientDrawable().apply {
        cornerRadius = 20f
        color = ColorStateList.valueOf(Color.parseColor(companyData.backgroundColor))
    }
    this.background = back
    this.layoutParams?.let {
        it.height = RandUtil.getRandInt(250, 600)
        this.layoutParams = it
    }
}

@BindingAdapter("textFromCompany")
fun TextView.setTextCompany(companyData: CompanyData) {
    val back = GradientDrawable().apply {
        cornerRadius = 20f
        color = ColorStateList.valueOf(Color.parseColor(companyData.backgroundColor))
    }
    this.background = back
    this.text = companyData.logo
    this.setTextColor(Color.parseColor(companyData.textColor))
}

@BindingAdapter("visibleProgressBar")
fun ProgressBar.setVisibleProgressBar(state: Boolean) {
    if (state) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

@BindingAdapter("graphData")
fun StockGraphView.setGraphData(list: List<StockItem>?) {
    list?.let {
        this.updateGraph(it)
    }
}

@BindingAdapter("update")
fun CircleProgressView.update(percent: Int?) {
    percent?.let {
        this.update(percent)
    }
}

@BindingAdapter("visibilityIfEmpty")
fun ConstraintLayout.setVisibilityIfEmpty(isEmpty: Boolean) {
    if (isEmpty) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}