package com.ihrsachin.sensoreventmanageger

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StepRecordingFragmentViewModel : ViewModel() {
    var count = MutableLiveData<Int>()

    init{
        count.value = 0
    }
    fun updateCount(){
        count.value = (count.value)?.plus(1)
    }
}