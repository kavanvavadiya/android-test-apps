package com.example.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
//    private var count = 0
    var count = MutableLiveData<Int>()

    init{
        count.value = 0
    }
    fun updateCount(){
        count.value = (count.value)?.plus(1)
    }
//    fun getCurrentCount():Int{
//        return count
//    }
//    fun getUpdatedCount():Int{
//        return ++count
//    }
}