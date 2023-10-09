package com.onban.kauantapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onban.kauantapp.data.SimilarNewsModel
import com.onban.kauantapp.data.ViewModelEvent
import com.onban.kauantapp.repo.Repository
import com.onban.network.data.CompanyData
import com.onban.network.data.NetworkResponse
import com.onban.network.data.NewsData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AnalysisViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private var _mainNewsData = MutableLiveData<NewsData>()
    val mainNewsData: LiveData<NewsData> = _mainNewsData

    private var _company = MutableLiveData<CompanyData>()
    val company: LiveData<CompanyData> = _company

    fun setMainNews(newsData: NewsData) {
        _mainNewsData.value = newsData
    }

    fun setCompany(companyData: CompanyData) {
        _company.value = companyData
    }

    private var _selectedSimilarNews = MutableLiveData<SimilarNewsModel>()
    val selectedSimilarNews: LiveData<SimilarNewsModel> = _selectedSimilarNews

    fun setSelectedSimilarNews(position: Int) {
        _similarNewsList.value?.let {
            _selectedSimilarNews.value = it[position]
        }
    }

    private var _fetchLock = MutableLiveData(false)
    val fetchLock: LiveData<Boolean> = _fetchLock

    private var _similarNewsList = MutableLiveData<List<SimilarNewsModel>>()
    val similarNewsList: LiveData<List<SimilarNewsModel>> = _similarNewsList
    private var similarNewsPageNo = 0

    fun fetchNextSimilarityNews() {
        fetchLock.value?.let { lock ->
            if (lock) {
                return
            }
            _fetchLock.value = true

            _mainNewsData.value?.let { newsData ->

                viewModelScope.launch {
                    when (val res = repository.getSimilarityNews(newsData.newsNo, similarNewsPageNo++)) {
                        is NetworkResponse.Success -> {
                            _similarNewsList.value?.let {
                                _similarNewsList.value = it.plus(res.body)
                            } ?: run {
                                _similarNewsList.value = res.body
                            }
                        }
                        is NetworkResponse.ApiError -> {
                            if (res.code == 203) {
                                // 페이지 끝
                                _fetchLock.value = false
                            }
                        }
                        else -> {
                            _fetchLock.value = false
                            triggerNetworkErrorEvent("서버와 연결이 끊겼습니다 :(")
                        }
                    }
                }
            }
        }
    }

    fun setFetchEnable() {
        _fetchLock.value = false
    }

    private val eventChannel = Channel<ViewModelEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    private fun triggerNetworkErrorEvent(msg: String) {
        viewModelScope.launch {
            eventChannel.send(ViewModelEvent.NetworkError(msg))
        }
    }
}