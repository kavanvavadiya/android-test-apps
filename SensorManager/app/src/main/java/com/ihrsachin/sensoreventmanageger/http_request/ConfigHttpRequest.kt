package com.ihrsachin.sensoreventmanageger.http_request


import android.content.Context
import android.util.Log
import com.ihrsachin.sensoreventmanageger.MarkerBasedFragmentViewModel
//import com.project_ips.piv0.databinding.FragmentPositionBinding
//import com.project_ips.piv0.databinding.FragmentTestBinding
import com.ihrsachin.sensoreventmanageger.models.*
//import com.project_ips.piv0.screens.MapsFragmentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ConfigHttpRequest(
    val viewModel: MarkerBasedFragmentViewModel,
    context: Context
): AsyncTask(context)  {
    private val baseUrl = "http://ec2-65-1-93-114.ap-south-1.compute.amazonaws.com/api/places/" //"http://cms.mapit.ai/api/places"
    private val TAG = "ConfigHttpRequest"
    fun initialize(){
        onPreExecute()
        Log.d(TAG, "initializing")
        var result = ""
        GlobalScope.launch(Dispatchers.IO){
            Log.d(TAG, "map Id : ${viewModel.currMap.value!!.id}")
            try {
                result = getJsonResponse()
            } catch (e: Exception) {
                Log.d(TAG, "error: $e")
//            binding.mapView.setImageResource(android.R.drawable.ic_dialog_alert)
//                return@launch
            }
            withContext(Dispatchers.Main) {
                Log.d(TAG, result)
                try {
                    updateBeaconConfigs(result)
                } catch (e: Exception) {
                }
                finally {
                    onPostExecute()
                }
            }
        }
    }


    private suspend fun getJsonResponse(): String {
//        assert(viewModel.currMap.value!!.id >= 0)
//        if(viewModel.currMap.value!!.id == -1) return ""
        val url = URL("$baseUrl/${viewModel.currMap.value!!.id}?format=json")
        Log.d(TAG, "url: ${url.content}")
        val httpConnection = withContext(Dispatchers.IO) {
            url.openConnection()
        } as HttpURLConnection
        httpConnection.requestMethod = "GET"
        httpConnection.readTimeout = 10000
        httpConnection.connectTimeout = 10000
        val inputStream = httpConnection.inputStream
        val res = inputStream.bufferedReader().use { it.readText() }
        httpConnection.disconnect()
        return res
    }

    private fun updateBeaconConfigs(jsonResponse: String){
        val root = JSONObject(jsonResponse)
        val jsonArray = root.getJSONArray("beacon_configs")
        val jsonBeaconConfigs: ArrayList<JSONBeaconConfig> = arrayListOf()
        for (i in 0 until jsonArray.length()) {
            val jsonObj = jsonArray.getJSONObject(i)
            val jsonBeaconConfig = JSONBeaconConfig(
                jsonObj.getString("type"),
                "null",
                jsonObj.getInt("ad_interval"),
                jsonObj.getString("packet"),
                jsonObj.getString("uuid"),
                jsonObj.getInt("major"),
                jsonObj.getInt("minor"),
                Position3(
                    jsonObj.getDouble("x_coordinate"),
                    jsonObj.getDouble("y_coordinate"),
                    jsonObj.getDouble("z_coordinate")
                ),
                1.0
            )
            Log.d(TAG, "floorPlan: ${jsonObj.getString("friendly_name")}")
            jsonBeaconConfigs.add(jsonBeaconConfig)
        }

        val testConfig = TestConfig(
            root.getString("location"),
            "null",
            jsonBeaconConfigs,
            arrayListOf(),
            pathLossConfig = PathLossConfig(1f, 1f),
            arrayListOf()
        )
        viewModel.currMap.value!!.testConfig = testConfig
        Log.d(TAG, "beaconConfig updated")
    }
}