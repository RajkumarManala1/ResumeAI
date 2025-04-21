package com.example.resumeai

import androidx.room.*

@Dao
interface ResumeDao {
    @Insert
    suspend fun insert(resume: Resume): Long

    @Query("SELECT * FROM resumes")
    suspend fun getAllResumes(): List<Resume>

    @Query("SELECT * FROM resumes WHERE id = :resumeId")
    suspend fun getResumeById(resumeId: Long): Resume?

    @Update
    suspend fun update(resume: Resume)

    @Delete
    suspend fun delete(resume: Resume)

    @Query("SELECT * FROM resumes ORDER BY id DESC LIMIT 1")
    suspend fun getLatestResume(): Resume?

    @Query("SELECT * FROM resumes ORDER BY timestamp DESC")
    suspend fun getAllResumesSorted(): List<Resume>
}
