package com.ihrsachin.sensoreventmanageger

import android.os.SystemClock
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ihrsachin.sensoreventmanageger.models.Destination
import com.ihrsachin.sensoreventmanageger.models.Map

class MarkerBasedFragmentViewModel:ViewModel() {
    val currentMarker = MutableLiveData(0)

    var markerRecords = LinkedHashMap<Long, Int>()
    val destination = MutableLiveData<Destination>()
    val mapList = MutableLiveData<ArrayList<Map>>()

    val currMap = MutableLiveData<Map>()

    init{
        destination.value = Destination.NONE
        mapList.value = arrayListOf()
    }
    /**
     * This should get called iff a marker button was pressed. It is responsible for recording marker data.
     */
    fun incrementMarker(){
        markerRecords[SystemClock.elapsedRealtimeNanos()/1000] = currentMarker.value!!
        currentMarker.value = currentMarker.value?.plus(1)
    }

//
//    // Current Map
//    val currMap = MutableLiveData<SVGMap>()


//    init{
//        currMap.value = SVGMap("h17_room.json")
//    }
}