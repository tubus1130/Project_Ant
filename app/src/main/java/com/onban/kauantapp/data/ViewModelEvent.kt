package com.onban.kauantapp.data

sealed class ViewModelEvent {
    data class NetworkError(val message: String) : ViewModelEvent()
}