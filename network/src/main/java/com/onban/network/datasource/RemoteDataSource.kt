package com.onban.network.datasource

import com.onban.network.api.NewsApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val newsApi: NewsApi
) {
    suspend fun getCompanyNews(pageNo: Int, company: String) = newsApi.getNews(pageNo, company)
    suspend fun getCompanyList() = newsApi.getCompanyList()
    suspend fun getAnalysisData(newsNo: String) = newsApi.getAnalysisData(newsNo)
    suspend fun getSimilarityNews(newsNo: String, pageNo: Int) = newsApi.getSimilarityNews(newsNo, pageNo)
}