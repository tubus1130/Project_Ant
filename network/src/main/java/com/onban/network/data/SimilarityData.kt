package com.onban.network.data

import com.google.gson.annotations.SerializedName

data class SimilarityNewsResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("result") val similarityDataList: List<SimilarityData>
)
data class SimilarityData(
    @SerializedName("newsNo") val newsNo: String,
    @SerializedName("dbNo") val similarNewsNo: String,
    @SerializedName("cosine") val similarity: Float,
)
