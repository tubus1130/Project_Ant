package com.onban.network.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CompanyData (
    @SerializedName("name") val name: String,
    @SerializedName("logo") val logo: String,
    @SerializedName("backgroundColor") val backgroundColor: String,
    @SerializedName("textColor") val textColor: String,
) : Serializable
data class CompanyResponse (
    @SerializedName("code") val code: Int,
    @SerializedName("result") val result: List<CompanyData>
)