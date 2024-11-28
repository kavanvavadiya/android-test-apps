package com.ihrsachin.sensoreventmanageger.http_request

import android.app.ProgressDialog
import android.content.Context
import com.ihrsachin.sensoreventmanageger.http_request.AsyncTaskIF
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

open class AsyncTask(val context: Context) : AsyncTaskIF {
    private lateinit var proDialog : ProgressDialog

    override fun onPreExecute(){
        MainScope().launch {
            proDialog = ProgressDialog(context)
            proDialog.setTitle("Project IPS")
            proDialog.setMessage("Loading...")
            proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            proDialog.setCancelable(true)
            proDialog.show()
        }
    }
    override fun onPostExecute(){
        MainScope().launch {
            proDialog.dismiss()
        }
    }
}
