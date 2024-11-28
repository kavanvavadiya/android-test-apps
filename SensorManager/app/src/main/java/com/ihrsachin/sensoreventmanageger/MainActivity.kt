package com.ihrsachin.sensoreventmanageger

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.ihrsachin.sensoreventmanageger.utility.startNewActivity

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_STORAGE_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userPreferences = UserPreferences(this)

        userPreferences.authLoginToken.asLiveData().observe(this, Observer {
            val activity = if (it == null) AuthActivity::class.java else HomeActivity::class.java
            startNewActivity(activity)
        })

        // Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission already granted, you can proceed with writing to external storage
            Log.d("StoragePermission", ": Already Granted!")
        } else {
            // Permission not granted, request it using SAF

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Log.d("StoragePermission", "Will request later")
                // Code for API level 30 or higher (Android 11+)
                // Scoped storage or SAF approach
                //requestStorageAccess()
            } else {
                Log.d("StoragePermission", ": Requesting Permission...")
                // Code for API level below 30
                // Traditional permission-based approach
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            }

        }
    }


    private fun requestStorageAccess() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, REQUEST_CODE_STORAGE_PERMISSION)
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && resultCode == RESULT_OK) {
            // Permission granted, you can proceed with writing to external storage
            Log.d("StoragePermission", ": Granted!")
            // Save the permission tree URI for future access
            val permissionUri = data?.data
            // Save the permissionUri to your user preferences or somewhere else
        } else {
            // Permission denied or request canceled
            Log.d("StoragePermission", ": Denied!")
        }
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with writing to external storage
                Log.d("StoragePermission", ": Granted!")
            } else {
                // Permission denied, handle the case where the user denied the permission
                Log.d("StoragePermission", ": Denied!")
            }
        }
    }
}
