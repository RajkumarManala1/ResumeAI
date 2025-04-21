package com.example.resumeai

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FileItemAdapter(
    private var fileList: MutableList<Any>,
    private val context: Context
) : RecyclerView.Adapter<FileItemAdapter.FileItemViewHolder>() {

    class FileItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileNameTextView: TextView = itemView.findViewById(R.id.tvFileName)
        val moreOptionsImageView: ImageView = itemView.findViewById(R.id.ivMoreOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.file_item, parent, false)
        return FileItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FileItemViewHolder, position: Int) {
        val file = fileList[position]

        when (file) {
            is Resume -> holder.fileNameTextView.text = file.fileName
            is CoverLetter -> holder.fileNameTextView.text = file.fileName
        }

        holder.fileNameTextView.setOnClickListener {
            if (file is Resume) {
                (context as HomeActivity).openPdfFile(file.filePath)
            }
        }

        holder.fileNameTextView.setOnLongClickListener {
            showFileOptionsPopup(holder.itemView, file)
            true
        }

        holder.moreOptionsImageView.setOnClickListener { view ->
            showFileOptionsPopup(view, file)
        }
    }

    override fun getItemCount(): Int = fileList.size

    fun updateData(newList: MutableList<Any>) {
        fileList = newList
        notifyDataSetChanged()
    }

    private fun showFileOptionsPopup(view: View, file: Any) {
        val popup = PopupMenu(context, view)
        popup.menuInflater.inflate(R.menu.file_actions_menu, popup.menu)

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_share -> {
                    (context as HomeActivity).shareFile((file as Resume).filePath)
                    true
                }
                R.id.action_delete -> {
                    deleteFile(file)
                    (context as HomeActivity).updateEmptyStateUI()
                    true
                }
                R.id.action_rename -> {
                    (context as HomeActivity).renameFileDialog(file as Resume)
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private fun deleteFile(file: Any) {
        fileList.remove(file)
        notifyDataSetChanged()
        // Implement database delete logic here
        when(file) {
            is Resume -> {
                // Delete Resume from DB
            }
            is CoverLetter -> {
                // Delete Cover letter from DB
            }
        }
    }
}
