package com.onban.network.data

import com.google.gson.annotations.SerializedName

data class AnalysisData(
    @SerializedName("company") val companyName: String,
    @SerializedName("date") val date: String,
    @SerializedName("fluctuation") val fluctuation: String,
)
data class AnalysisNewsResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("result") val newsData: NewsData,
    @SerializedName("result2") val analysisDataList: List<AnalysisData>,
)
