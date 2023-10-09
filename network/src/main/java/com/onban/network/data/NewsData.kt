package com.onban.network.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NewsData(
    @SerializedName("newsNo") val newsNo: String,
    @SerializedName("title") val title: String,
    @SerializedName("newsDate") val date: String,
    @SerializedName("description") val desc: String,
    @SerializedName("url") val newsUrl: String,
    @SerializedName("company") val company: String,
) : Serializable