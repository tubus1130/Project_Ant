package com.onban.network.data

import com.google.gson.annotations.SerializedName

data class CompanyNewsResponse(
    @SerializedName("result") val newsList: List<NewsData>
)
