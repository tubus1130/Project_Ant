package com.onban.kauantapp.data

data class SimilarNewsModel (
    val date: String,
    val title: String,
    val description: String,
    val similarity: Int,
    val url: String,
    val stockPriceFluctuationList: List<StockItem>
)

data class StockItem(
    val value: Float,
    val date: String,
)