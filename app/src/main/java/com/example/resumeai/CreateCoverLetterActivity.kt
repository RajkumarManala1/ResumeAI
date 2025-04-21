package com.example.resumeai

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import android.widget.EditText
import android.widget.Button
import android.widget.Toast

class CreateCoverLetterActivity : AppCompatActivity() {

    private lateinit var coverLetterDao: CoverLetterDao
    private lateinit var etCompanyName: EditText
    private lateinit var etPosition: EditText
    private lateinit var etContent: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cover_letter)

        coverLetterDao = AppDatabase.getDatabase(this).coverLetterDao()

        etCompanyName = findViewById(R.id.etCompanyName)
        etPosition = findViewById(R.id.etPosition)
        etContent = findViewById(R.id.etContent)
        btnSave = findViewById(R.id.btnSave)

        Log.d("CreateCoverLetterActivity", "CreateCoverLetterActivity started")

        btnSave.setOnClickListener {
            saveCoverLetter()
        }
    }

    private fun saveCoverLetter() {
        val companyName = etCompanyName.text.toString().trim()
        val position = etPosition.text.toString().trim()
        val content = etContent.text.toString().trim()

        if (companyName.isNotEmpty() && position.isNotEmpty() && content.isNotEmpty()) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val newCoverLetter = CoverLetter(
                        companyName = companyName,
                        position = position,
                        content = content
                    )
                    val coverLetterId = coverLetterDao.insert(newCoverLetter)
                    Log.d("CreateCoverLetterActivity", "Cover letter inserted with ID: $coverLetterId")

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CreateCoverLetterActivity, "Cover letter saved", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }
}