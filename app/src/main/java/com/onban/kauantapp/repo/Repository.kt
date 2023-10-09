package com.onban.kauantapp.repo

import com.onban.kauantapp.data.SimilarNewsModel
import com.onban.kauantapp.data.StockItem
import com.onban.network.data.AnalysisNewsResponse
import com.onban.network.data.NetworkResponse
import com.onban.network.data.SimilarityData
import com.onban.network.datasource.RemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.lang.Error
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun getCompanyNews(pageNo: Int, company: String) = remoteDataSource.getCompanyNews(pageNo, company)

    suspend fun getCompanyList() = remoteDataSource.getCompanyList()

    suspend fun getSimilarityNews(newsNo: String, pageNo: Int): NetworkResponse<List<SimilarNewsModel>, Error> {
        when (val res = remoteDataSource.getSimilarityNews(newsNo, pageNo)) {
            is NetworkResponse.Success -> {
                if (res.body.code != 200) {
                    return NetworkResponse.ApiError(Error("페이지 끝"), res.body.code)
                }
                val similarityNewsModelList = res.body.similarityDataList.map { simData ->
                    val diff = CoroutineScope(Dispatchers.IO).async {
                        remoteDataSource.getAnalysisData(simData.similarNewsNo)
                    }
                    when (val similarityRes = diff.await()) {
                        is NetworkResponse.Success -> {
                            similarityRes.body.newsData
                            similarityRes.body.analysisDataList
                            toSimilarNewsModel(simData, similarityRes.body)
                        }
                        is NetworkResponse.ApiError -> {
                            return similarityRes
                        }
                        is NetworkResponse.NetworkError -> {
                            return similarityRes
                        }
                        is NetworkResponse.UnknownError -> {
                            return similarityRes
                        }
                    }
                }
                return NetworkResponse.Success(similarityNewsModelList)
            }
            is NetworkResponse.ApiError -> {
                return res
            }
            is NetworkResponse.NetworkError -> {
                return res
            }
            is NetworkResponse.UnknownError -> {
                return res
            }
        }
    }


    private fun toSimilarNewsModel(similarityData: SimilarityData, analysisNewsResponse: AnalysisNewsResponse): SimilarNewsModel =
        SimilarNewsModel(
            date = analysisNewsResponse.newsData.date,
            title = analysisNewsResponse.newsData.title,
            description = analysisNewsResponse.newsData.desc,
            similarity = similarityData.similarity.toInt(),
            url = analysisNewsResponse.newsData.newsUrl,
            stockPriceFluctuationList = analysisNewsResponse.analysisDataList.map {
                StockItem(
                    fluctuationToFloat(it.fluctuation),
                    it.date
                )
            }
        )

    private fun fluctuationToFloat(fluctuation: String): Float {
        return when(fluctuation.first()) {
            '+' -> fluctuation.substring(1, fluctuation.length).toFloat()
            '-' -> fluctuation.substring(1, fluctuation.length).toFloat() * -1
            else -> 0f
        }
    }
}