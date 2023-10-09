package com.onban.kauantapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onban.kauantapp.data.ViewModelEvent
import com.onban.kauantapp.repo.Repository
import com.onban.network.data.NetworkResponse
import com.onban.network.data.NewsData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private var _mainNewsList = MutableLiveData<List<NewsData>>()
    val mainNewsList: LiveData<List<NewsData>> = _mainNewsList
    private var mainNewsPageNo = 0

    // flag 설정 말고 다른 옵션이 있을까?
    // fetch 시작 ~ submitList 완료 까지  fetch 하지 못하게 막는다
    private var _fetchLock = MutableLiveData(false)
    val fetchLock: LiveData<Boolean> = _fetchLock

    private var companyName = ""

    fun fetchNextNews() {
        fetchLock.value?.let { lock ->
            if (!lock) {
                _fetchLock.value = true
                viewModelScope.launch {
                    // company는 나중에 변수로 변경해야 함
                    when (val res = repository.getCompanyNews(mainNewsPageNo++, companyName)) {
                        is NetworkResponse.Success -> {
                            _mainNewsList.value?.let {
                                _mainNewsList.value = it.plus(res.body.newsList)
                            } ?: run {
                                _mainNewsList.value = res.body.newsList
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

    fun setCompany(companyName: String) {
        this.companyName = companyName
    }

    private val eventChannel = Channel<ViewModelEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    private fun triggerNetworkErrorEvent(msg: String) {
        viewModelScope.launch {
            eventChannel.send(ViewModelEvent.NetworkError(msg))
        }
    }
}