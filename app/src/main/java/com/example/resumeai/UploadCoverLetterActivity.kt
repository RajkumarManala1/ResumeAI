package com.example.resumeai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import androidx.lifecycle.lifecycleScope
import android.widget.Button
import android.widget.TextView
import android.widget.ProgressBar
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class UploadCoverLetterActivity : AppCompatActivity() {

    private val REQUEST_CODE_PICK_FILE = 100
    private lateinit var coverLetterDao: CoverLetterDao // Changed to CoverLetterDao
    private lateinit var tvFileName: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvUploadStatus: TextView
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_cover_letter) // Changed to cover letter layout

        coverLetterDao = AppDatabase.getDatabase(this).coverLetterDao() // Changed to cover letter dao

        val btnUpload: Button = findViewById(R.id.btnUpload)
        tvFileName = findViewById(R.id.tvFileName)
        progressBar = findViewById(R.id.progressBar)
        tvUploadStatus = findViewById(R.id.tvUploadStatus)
        btnBack = findViewById(R.id.btnBack)

        btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, this.REQUEST_CODE_PICK_FILE)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            Log.d("UploadCoverLetterActivity", "File URI: $uri") // Changed log tag

            if (uri != null) {
                val fileName = getFileNameFromUri(uri)
                tvFileName.text = "Selected File: $fileName"
                readFileContent(uri, fileName)
            }

        } else {
            Log.d("UploadCoverLetterActivity", "File selection cancelled or error") // Changed log tag
            finish()
        }
    }

    private fun readFileContent(uri: android.net.Uri, fileName: String?) {
        progressBar.visibility = View.VISIBLE
        tvUploadStatus.text = "Uploading"

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line).append("\n")
                        line = reader.readLine()
                    }
                    inputStream?.close()

                    val originalContent = stringBuilder.toString()
                    Log.d("UploadCoverLetterActivity", "File Content: $originalContent") // Changed log tag

                    // Save file to internal storage
                    val filePath = saveFileToInternalStorage(uri, fileName)

                    // Create CoverLetter object and insert into database
                    val coverLetter = CoverLetter( // Changed to CoverLetter object
                        content = originalContent,
                        fileName = fileName,
                        filePath = filePath
                    )
                    val coverLetterId = coverLetterDao.insert(coverLetter) // Changed to coverLetterDao
                    Log.d("UploadCoverLetterActivity", "CoverLetter inserted with ID: $coverLetterId") // Changed log tag

                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        tvUploadStatus.text = "Upload Successful!"
                        Toast.makeText(this@UploadCoverLetterActivity, "Cover letter uploaded successfully", Toast.LENGTH_SHORT).show() // Changed toast message
                        finish() // Finish the activity
                    }

                } catch (e: Exception) {
                    Log.e("UploadCoverLetterActivity", "Error reading file: ${e.message}") // Changed log tag
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        tvUploadStatus.text = "Upload Failed"
                        Toast.makeText(this@UploadCoverLetterActivity, "Error uploading file", Toast.LENGTH_SHORT).show()
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
            Log.e("UploadCoverLetterActivity", "Error saving file: ${e.message}") // Changed log tag
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