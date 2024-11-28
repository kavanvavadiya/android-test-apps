package com.example.twowaydata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    val username = MutableLiveData<String>()
    init{
        username.value = "Kavan"
    }
}