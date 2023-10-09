package com.onban.kauantapp.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.onban.kauantapp.R
import com.onban.kauantapp.common.adapter.SimilarNewsListAdapter
import com.onban.kauantapp.common.app.GlobalApp
import com.onban.kauantapp.common.view.BaseActivity
import com.onban.kauantapp.data.ViewModelEvent
import com.onban.kauantapp.databinding.ActivityAnalysisBinding
import com.onban.kauantapp.viewmodel.AnalysisViewModel
import com.onban.network.data.CompanyData
import com.onban.network.data.NewsData
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class AnalysisActivity : BaseActivity<ActivityAnalysisBinding>() {

    private lateinit var adapter: SimilarNewsListAdapter
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: AnalysisViewModel by viewModels { viewModelFactory}

    private val submitListCallback = Runnable {
        viewModel.setFetchEnable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        collectEvent()
        initViewModel()
        initAdapter()
        initViews()
    }

    override fun createBinding(): ActivityAnalysisBinding {
        return ActivityAnalysisBinding.inflate(layoutInflater)
    }

    override fun inject() {
        (applicationContext as GlobalApp).appComponent.inject(this)
    }

    private fun setBinding() {
        with(binding) {
            activity = this@AnalysisActivity
            viewModel = this@AnalysisActivity.viewModel
            submitListCallback = this@AnalysisActivity.submitListCallback
        }
    }

    private fun initAdapter() {
        adapter = SimilarNewsListAdapter {
            moveToWebActivity(it.url)
        }
    }

    private fun collectEvent() {
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

    private fun initViewModel() {
        viewModel.setMainNews(intent.getSerializableExtra("newsData") as NewsData)
        viewModel.setCompany(intent.getSerializableExtra("company") as CompanyData)
        viewModel.fetchNextSimilarityNews()
    }

    private fun initViews() {
        setTitleNews()
        setViewPager()
    }

    private fun setTitleNews() {
        binding.cnstlTitle.setOnClickListener {
            viewModel.mainNewsData.value?.let {
                moveToWebActivity(it.newsUrl)
            }
        }
    }

    private fun setViewPager() {
        with(binding) {
            vp2Analysis.adapter = adapter

            vp2Analysis.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            vp2Analysis.offscreenPageLimit = 2
            val pageMargin = resources.getDimensionPixelOffset(R.dimen.pageMargin).toFloat()
            val pageOffset = resources.getDimensionPixelOffset(R.dimen.offset).toFloat()
            vp2Analysis.setPageTransformer { page, position ->
                val myOffset = position * -(2 * pageOffset + pageMargin)
                if (position < -1) {
                    page.translationX = -myOffset
                } else if (position <= 1) {
                    val scaleFactor = Math.max(0.7f, 1 - Math.abs(position - 0.14285715f))
                    page.translationX = myOffset
                    page.scaleY = scaleFactor
                    page.alpha = scaleFactor
                } else {
                    page.alpha = 0f
                    page.translationX = myOffset
                }
            }
            vp2Analysis.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (adapter.itemCount - 1 == position) {
                        this@AnalysisActivity.viewModel.fetchNextSimilarityNews()
                    }
                    this@AnalysisActivity.viewModel.setSelectedSimilarNews(position)
                }
            })
        }
    }

    private fun moveToWebActivity(url: String) {
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtra("url", url)
        startActivity(intent)
    }
}