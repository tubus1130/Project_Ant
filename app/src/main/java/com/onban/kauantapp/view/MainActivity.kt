package com.onban.kauantapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.onban.kauantapp.R
import com.onban.kauantapp.common.adapter.MainListAdapter
import com.onban.kauantapp.common.adapter.StickyHeaderItemDecoration
import com.onban.kauantapp.common.app.GlobalApp
import com.onban.kauantapp.common.view.BaseActivity
import com.onban.kauantapp.data.ViewModelEvent
import com.onban.kauantapp.databinding.ActivityMainBinding
import com.onban.kauantapp.viewmodel.MainViewModel
import com.onban.network.data.CompanyData
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private lateinit var adapter: MainListAdapter
    @Inject lateinit var viewModel: MainViewModel
    private lateinit var companyData: CompanyData

    private val submitListCallback = Runnable {
        viewModel.setFetchEnable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        initViews()
        initData()
        observeEvent()
    }

    override fun createBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    private fun setBinding() {
        with(binding) {
            viewmodel = viewModel
            submitListCallback = this@MainActivity.submitListCallback
        }
    }

    override fun inject() {
        // Make Dagger instantiate @Inject fields in MainActivity
        (applicationContext as GlobalApp).appComponent.inject(this)
    }

    private fun initViews() {
        adapter = MainListAdapter {
            val intent = Intent(this, AnalysisActivity::class.java)
            intent.putExtra("newsData", it)
            intent.putExtra("company", companyData)
            startActivity(intent)
        }

        with(binding) {
            rcvMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    rcvMain.adapter?.let { adapter ->
                        val lastVisibleItemPosition = (rcvMain.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        val lastItemPosition = adapter.itemCount - 1
                        if (lastVisibleItemPosition == lastItemPosition) {
                            viewModel.fetchNextNews()
                        }
                    }
                }
            })
            rcvMain.adapter = adapter
            rcvMain.addItemDecoration(StickyHeaderItemDecoration(getSectionCallback()))
        }
    }

    private fun initData() {
        companyData = intent.getSerializableExtra("company") as CompanyData
        binding.company = companyData
        viewModel.setCompany(companyData.name)
        viewModel.fetchNextNews()
    }

    private fun getSectionCallback(): StickyHeaderItemDecoration.SectionCallback {
        return object : StickyHeaderItemDecoration.SectionCallback {
            override fun isHeader(position: Int): Boolean {
                return adapter.isHeader(position)
            }

            override fun getHeaderLayoutView(list: RecyclerView, position: Int): View? {
                return adapter.getHeaderView(list, position)
            }
        }
    }

    private fun observeEvent() {
        lifecycleScope.launchWhenStarted {
            viewModel.eventFlow.collect {
                when (it) {
                    is ViewModelEvent.NetworkError -> {
                        Snackbar.make(binding.root, it.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}