package com.example.resumeai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import androidx.core.view.isVisible

class HomeActivity : AppCompatActivity() {

    private lateinit var menuIcon: ImageView
    private lateinit var fabAdd: ImageView
    private lateinit var resumeTab: LinearLayout
    private lateinit var coverLetterTab: LinearLayout
    private lateinit var resumeUnderline: View
    private lateinit var coverLetterUnderline: View
    private lateinit var recyclerViewFiles: RecyclerView
    private lateinit var fileItemAdapter: FileItemAdapter
    private lateinit var resumeContentTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.hide()

        menuIcon = findViewById(R.id.menuIcon)
        fabAdd = findViewById(R.id.fabAdd)
        resumeTab = findViewById(R.id.resumeTab)
        coverLetterTab = findViewById(R.id.coverLetterTab)
        resumeUnderline = findViewById(R.id.resumeUnderline)
        coverLetterUnderline = findViewById(R.id.coverLetterUnderline)
        recyclerViewFiles = findViewById(R.id.recyclerViewFiles)
        resumeContentTextView = findViewById(R.id.resumeContentTextView)

        fileItemAdapter = FileItemAdapter(mutableListOf(), this)
        recyclerViewFiles.layoutManager = LinearLayoutManager(this)
        recyclerViewFiles.adapter = fileItemAdapter

        menuIcon.setOnClickListener { showPopupMenu(it) }
        fabAdd.setOnClickListener { showAddOptionsDialog() }

        resumeTab.setOnClickListener {
            showResumePage()
            resumeUnderline.visibility = View.VISIBLE
            coverLetterUnderline.visibility = View.GONE
            onResume()
        }

        coverLetterTab.setOnClickListener {
            showCoverLetterPage()
            coverLetterUnderline.visibility = View.VISIBLE
            resumeUnderline.visibility = View.GONE
            onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (resumeUnderline.isVisible) {
            loadLatestResume()
        } else {
            loadLatestCoverLetter()
        }
    }

    fun shareFile(filePath: String?) {
        filePath?.let {
            val file = File(it)
            val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Share Resume"))
        }
    }

    fun renameFileDialog(resume: Resume) {
        val input = EditText(this).apply {
            hint = "Enter new name"
        }

        AlertDialog.Builder(this)
            .setTitle("Rename Resume")
            .setView(input)
            .setPositiveButton("Rename") { _, _ ->
                val newName = input.text.toString()
                if (newName.isNotBlank()) {
                    val file = File(resume.filePath)
                    val renamed = File(file.parent, "$newName.pdf")
                    if (file.renameTo(renamed)) {
                        resume.fileName = "$newName.pdf"
                        resume.filePath = renamed.absolutePath

                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                AppDatabase.getDatabase(this@HomeActivity).resumeDao().update(resume)
                            }
                            onResume()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun updateEmptyStateUI() {
        resumeContentTextView.visibility =
            if (fileItemAdapter.itemCount > 0) View.GONE else View.VISIBLE
    }

    private fun loadLatestResume() {
        Log.d("HomeActivity", "loadLatestResume called")
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(this@HomeActivity)
                val resumeDao = db.resumeDao()
                val latestResume = resumeDao.getLatestResume()

                withContext(Dispatchers.Main) {
                    if (latestResume != null) {
                        displayResume(latestResume)
                    } else {
                        clearResumeDisplay()
                    }
                    updateEmptyStateUI()
                }
            }
        }
    }

    private fun loadLatestCoverLetter() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(this@HomeActivity)
                val coverLetterDao = db.coverLetterDao()
                val latestCoverLetter = coverLetterDao.getLatestCoverLetter()

                withContext(Dispatchers.Main) {
                    if (latestCoverLetter != null) {
                        displayCoverLetter(latestCoverLetter)
                    } else {
                        clearCoverLetterDisplay()
                    }
                    updateEmptyStateUI()
                }
            }
        }
    }

    private fun displayResume(resume: Resume) {
        if (resume.fileName != null) {
            fileItemAdapter.updateData(mutableListOf(resume))
        } else {
            clearResumeDisplay()
        }
    }

    private fun displayCoverLetter(coverLetter: CoverLetter) {
        if (coverLetter.fileName != null) {
            fileItemAdapter.updateData(mutableListOf(coverLetter))
        } else {
            clearCoverLetterDisplay()
        }
    }

    private fun clearResumeDisplay() {
        fileItemAdapter.updateData(mutableListOf())
    }

    private fun clearCoverLetterDisplay() {
        fileItemAdapter.updateData(mutableListOf())
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = androidx.appcompat.widget.PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.home_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_my_account -> {
                    startActivity(Intent(this, MyAccountActivity::class.java))
                    true
                }
                R.id.action_languages -> true
                R.id.action_terms_privacy -> true
                R.id.action_contact_us -> true
                R.id.action_sign_out -> true
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showAddOptionsDialog() {
        val builder = AlertDialog.Builder(this)
        val options: Array<String>
        val title: String

        if (resumeUnderline.visibility == View.VISIBLE) {
            title = "Add Resume"
            options = arrayOf("Upload Resume", "Create Resume")
        } else {
            title = "Add Cover Letter"
            options = arrayOf("Upload Cover Letter", "Create Cover Letter")
        }

        builder.setTitle(title)
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> handleAddOption(options[0])
                1 -> handleAddOption(options[1])
            }
        }
        builder.show()
    }

    private fun handleAddOption(option: String) {
        if (option.contains("Resume")) {
            if (option.contains("Upload")) {
                handleUploadResume()
            } else {
                handleCreateResume()
            }
        } else {
            if (option.contains("Upload")) {
                handleUploadCoverLetter()
            } else {
                handleCreateCoverLetter()
            }
        }
    }

    private fun handleUploadResume() {
        startActivity(Intent(this, UploadResumeActivity::class.java))
    }

    private fun handleCreateResume() {
        startActivity(Intent(this, CreateResumeActivity::class.java))
    }

    private fun handleUploadCoverLetter() {
        startActivity(Intent(this, UploadCoverLetterActivity::class.java))
    }

    private fun handleCreateCoverLetter() {
        startActivity(Intent(this, CreateCoverLetterActivity::class.java))
    }

    fun openPdfFile(filePath: String?) {
        if (filePath != null) {
            val file = File(filePath)
            val uri: Uri = FileProvider.getUriForFile(
                this@HomeActivity,
                "${packageName}.provider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    this@HomeActivity,
                    "No PDF viewer app found.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this@HomeActivity, "File path is missing.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showResumePage() {
        Log.d("HomeActivity", "Showing Resume Page")
    }

    private fun showCoverLetterPage() {
        Log.d("HomeActivity", "Showing Cover Letter Page")
    }
}