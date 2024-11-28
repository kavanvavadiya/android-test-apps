package com.ihrsachin.sensoreventmanageger

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.ihrsachin.sensoreventmanageger.databinding.FragmentFinalBinding
import java.io.File


class FinalFragment : Fragment() {

    lateinit var binding : FragmentFinalBinding
    val REQUEST_CODE_STORAGE_PERMISSION = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate(inflater, R.layout.fragment_final, container, false)


        binding.textView13.setOnClickListener{
            checkPermissionAndSave()
        }
        return binding.root
    }

    fun checkPermissionAndSave(){
        // Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission already granted, you can proceed with writing to external storage
            Log.d("StoragePermission", ": Already Granted!")
            saveToExternalStorage()
        } else {
            // Permission not granted, request it using SAF
            Log.d("StoragePermission", ": Requesting Permission...")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Code for API level 30 or higher (Android 11+)
                // Scoped storage or SAF approach
                requestStorageAccess()
            } else {
                // Code for API level below 30
                // Traditional permission-based approach
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            }

        }
    }


    private fun requestStorageAccess() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        requestPermissionLauncher.launch(intent)
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            // Permission granted, you can proceed with writing to external storage
            Log.d("StoragePermission", ": Granted!")
            // Save the permission tree URI for future access
            val permissionUri = result.data?.data
            // Save the permissionUri to your user preferences or somewhere else
            Log.d("StoragePermission", "$permissionUri")

            permissionUri?.let { uri ->
                val uriBuilder = Uri.Builder()
                    .scheme(uri.scheme)
                    .authority(uri.authority)
                    .appendEncodedPath(uri.encodedPath)

                val contentUri = uriBuilder.build()

                requireContext().contentResolver.openOutputStream(contentUri)?.use { outputStream ->
                    val fileContent = "This is the content of the text file"

                    // Write the content of the file to the output stream
                    outputStream.write(fileContent.toByteArray())

                    // File saved successfully
                    Log.d("FileSave", "File saved: $contentUri")
                }
            }


        } else {
            // Permission denied or request canceled
            Log.d("StoragePermission", ": Denied!")
        }
    }

    private fun saveToExternalStorage() {
        val customFolderName = "MyCustomFolder"
        val customFolderPath = requireContext().getExternalFilesDir(null)?.absolutePath + File.separator + customFolderName
        val customFolder = File(customFolderPath)
        if (!customFolder.exists()) {
            customFolder.mkdirs()
        }

        val fileName = "myfile.txt"
        val file = File(customFolderPath, fileName)
        val fileContent = "This is the content of my file."

        file.writeText(fileContent) // Write the file content
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && resultCode == AppCompatActivity.RESULT_OK) {
            // Permission granted, you can proceed with writing to external storage
            Log.d("StoragePermission", ": Granted!")
            // Save the permission tree URI for future access
            val permissionUri = data?.data
            // Save the permissionUri to your user preferences or somewhere else
            Log.d("StoragePermission", "$permissionUri")

            permissionUri?.let { uri ->
                val contentUri = Uri.encode(uri.toString())

                requireContext().contentResolver.openOutputStream(Uri.parse(contentUri))?.use { outputStream ->
                    val fileContent = "This is the content of the text file"

                    // Write the content of the file to the output stream
                    outputStream.write(fileContent.toByteArray())

                    // File saved successfully
                    Log.d("FileSave", "File saved: $contentUri")
                }
            }

        } else {
            // Permission denied or request canceled
            Log.d("StoragePermission", ": Denied!")
        }
    }

}