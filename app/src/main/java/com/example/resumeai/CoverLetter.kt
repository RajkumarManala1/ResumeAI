package com.example.resumeai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cover_letters") // Changed table name
data class CoverLetter(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val companyName: String? = null, // Added companyName
    val position: String? = null,      // Added position
    val content: String? = null,       // Added content
    val filePath: String? = null,
    val fileName: String? = null
    // Add other fields as needed for your cover letter data
)