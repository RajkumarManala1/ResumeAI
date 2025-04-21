package com.example.resumeai

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope

class CreateResumeActivity : AppCompatActivity() {

    private lateinit var resumeDao: ResumeDao // Declare ResumeDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_resume)

        // Initialize ResumeDao
        resumeDao = AppDatabase.getDatabase(this).resumeDao()

        Log.d("CreateResumeActivity", "CreateResumeActivity started")

        // TODO: Implement UI for creating a resume
        // TODO: Get user input for resume sections

        // Example: Inserting a dummy resume (replace with actual data)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val newResume = Resume(
                    firstName = "UserFirstName",
                    lastName = "UserLastName",
                    email = "user@example.com"
                    // ... set other fields from user input ...
                )
                val resumeId = resumeDao.insert(newResume)
                Log.d("CreateResumeActivity", "Resume inserted with ID: $resumeId")
            }
        }

        // TODO: Potentially trigger resume enhancement
        // TODO: Finish this activity or navigate elsewhere
    }
}