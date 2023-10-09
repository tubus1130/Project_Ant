package com.onban.kauantapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onban.kauantapp.repo.Repository
import com.onban.network.data.CompanyData
import com.onban.network.data.NetworkResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private var _companyList = MutableLiveData<List<CompanyData>>()
    val companyList: LiveData<List<CompanyData>> = _companyList

    fun fetchCompanyList() {
        viewModelScope.launch {
            when(val res = repository.getCompanyList()) {
                is NetworkResponse.Success -> {
                    _companyList.value = res.body.result
                }
            }
        }
    }
}