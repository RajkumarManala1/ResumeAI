package com.example.resumeai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import android.widget.Button
import android.widget.TextView
import android.widget.ProgressBar
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class UploadResumeActivity : AppCompatActivity() {

    private val REQUEST_CODE_PICK_FILE = 100
    private lateinit var resumeDao: ResumeDao
    private lateinit var tvFileName: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvUploadStatus: TextView
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_resume)

        resumeDao = AppDatabase.getDatabase(this).resumeDao()

        val btnUpload: Button = findViewById(R.id.btnUpload)
        tvFileName = findViewById(R.id.tvFileName)
        progressBar = findViewById(R.id.progressBar)
        tvUploadStatus = findViewById(R.id.tvUploadStatus)
        btnBack = findViewById(R.id.btnBack)

        btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf" // Specify the file type here
            startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            Log.d("UploadResumeActivity", "File URI: $uri")

            if (uri != null) {
                val fileName = getFileNameFromUri(uri)
                tvFileName.text = "Selected File: $fileName"
                readFileContent(uri, fileName) // Pass fileName
            }

        } else {
            Log.d("UploadResumeActivity", "File selection cancelled or error")
            finish()
        }
    }

    private fun readFileContent(uri: android.net.Uri, fileName: String?) {
        progressBar.visibility = View.VISIBLE
        tvUploadStatus.text = "Uploading..."

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val inputStream: InputStream? = contentResolver.openInputStream(uri)
                    if (inputStream == null) {
                        throw Exception("Could not open input stream")
                    }

                    // Read the content from the stream directly
                    val originalContent = inputStream.readBytes().toString(Charsets.UTF_8)
                    inputStream.close() // Close the stream as soon as we're done reading
                    Log.d("UploadResumeActivity", "File Content: $originalContent")

                    // Save file to internal storage
                    val filePath = saveFileToInternalStorage(uri, fileName)

                    // Create Resume object and insert into database
                    val resume = Resume(
                        originalContent = originalContent,
                        fileName = fileName,
                        filePath = filePath
                    )
                    val resumeId = resumeDao.insert(resume)
                    Log.d("UploadResumeActivity", "Resume inserted with ID: $resumeId")

                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        tvUploadStatus.text = "Upload Successful!"
                        Toast.makeText(this@UploadResumeActivity, "Resume uploaded successfully", Toast.LENGTH_SHORT).show()

                        // Start HomeActivity
                        val intent = Intent(this@UploadResumeActivity, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish() // Finish the activity
                    }

                } catch (e: Exception) {
                    Log.e("UploadResumeActivity", "Error reading file: ${e.message}")
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        tvUploadStatus.text = "Upload Failed"
                        Toast.makeText(this@UploadResumeActivity, "Error uploading file", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun saveFileToInternalStorage(uri: android.net.Uri, fileName: String?): String? {
        if (fileName == null) return null
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            Log.e("UploadResumeActivity", "Error saving file: ${e.message}")
            null
        }
    }

    private fun getFileNameFromUri(uri: android.net.Uri): String? {
        var fileName: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    fileName = it.getString(displayNameIndex)
                }
            }
        }
        return fileName
    }
}