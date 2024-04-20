package com.example.prashantadvaitdemo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    //  Declare a MutableStateFlow for splashImageUrls
    private val _splashImageUrls = MutableStateFlow<List<String>>(emptyList())

    //  Create a public StateFlow
    val splashImageUrls: StateFlow<List<String>> = _splashImageUrls.asStateFlow()

    // Create a function to update splashImageUrls
    fun updateSplashImageUrls(newUrls: List<String>) {
        _splashImageUrls.value = newUrls
    }
}