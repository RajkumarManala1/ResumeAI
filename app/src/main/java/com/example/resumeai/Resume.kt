package com.example.resumeai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resumes")
data class Resume(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val summary: String? = null,
    val education: String? = null,
    val workExperience: String? = null,
    val skills: String? = null,

    var filePath: String? = null,
    var fileName: String? = null,

    val timestamp: Long = System.currentTimeMillis(),
    val originalContent: String? = null
)
