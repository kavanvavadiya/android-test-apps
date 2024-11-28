package com.ihrsachin.sensoreventmanageger.http_request

import android.content.Context
import android.util.Log
import android.view.View.VISIBLE
import com.ihrsachin.sensoreventmanageger.databinding.FragmentMarkerBasedBinding
import com.ihrsachin.sensoreventmanageger.databinding.FragmentRecordDataBinding
import com.ihrsachin.sensoreventmanageger.models.Map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import java.net.HttpURLConnection
import java.net.URL

class MapHttpRequest(context : Context, val binding : FragmentRecordDataBinding, val mapList : ArrayList<Map>) : AsyncTask(context){
    private val baseUrl = "http://cms.mapit.ai/api/places"
    private val TAG = "MapHttpRequest"
    fun initialize(){
        onPreExecute()
        GlobalScope.launch(Dispatchers.IO){
            var result = ""
            try {
                result = getJsonResponse()
            } catch (e : Exception){
                withContext(Dispatchers.Main){
//                    binding.imgView.visibility = VISIBLE
//                    binding.textView.text = "Error: $e\nTry Offline map"
//                    binding.textView.visibility = VISIBLE
                }
            }
            finally {
                onPostExecute()
            }

            if (result != "")
                withContext(Dispatchers.Main) {
                    Log.d(TAG, result)
                    try {
                        updateMapList(result)
                        onPostExecute()
                    } catch (e: JSONException) {
//                        binding.imgView.visibility = VISIBLE
//                        binding.textView.text = "Error: $e\nPull down to refresh"
//                        binding.textView.visibility = VISIBLE
                    }
                }
            else{
                mapList.add(Map(-1, "Offline Map", "null"))
            }
        }
    }


    private suspend fun getJsonResponse() : String{
        val url = URL("$baseUrl/?format=json")
        val httpConnection = withContext(Dispatchers.IO) {
            url.openConnection()
        } as HttpURLConnection
        httpConnection.requestMethod = "GET"
        httpConnection.connectTimeout = 10000
        httpConnection.readTimeout = 10000
        val inputStream = httpConnection.inputStream
        val res =  inputStream.bufferedReader().use { it.readText() }
        httpConnection.disconnect()
        return res
    }

    private fun updateMapList(jsonResponse: String){
        val jsonArray = JSONArray(jsonResponse)
        for(i in 0 until jsonArray.length()){
            val jsonObj = jsonArray.getJSONObject(i)
            val map = Map(
                jsonObj.getInt("id"),
                jsonObj.getString("name"),
                jsonObj.getString("floor_plan")
            )
            Log.d(TAG, "floorPlan: ${jsonObj.getString("floor_plan")}")
            mapList.add(map)
        }
    }
}