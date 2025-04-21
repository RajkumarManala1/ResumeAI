package com.example.resumeai

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CoverLetterDao {

    @Insert
    suspend fun insert(coverLetter: CoverLetter): Long

    @Query("SELECT * FROM cover_letters")
    suspend fun getAllCoverLetters(): List<CoverLetter>

    @Query("SELECT * FROM cover_letters WHERE id = :coverLetterId")
    suspend fun getCoverLetterById(coverLetterId: Long): CoverLetter?

    @Update
    suspend fun update(coverLetter: CoverLetter)

    @Delete
    suspend fun delete(coverLetter: CoverLetter)

    @Query("SELECT * FROM cover_letters ORDER BY id DESC LIMIT 1")
    suspend fun getLatestCoverLetter(): CoverLetter?
}