package com.github.example.ui

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.example.R

class IssueDialog : DialogFragment() {
    private lateinit var onInputListener: OnInputListener

    fun setOnInputListener(listener: OnInputListener) {
        onInputListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.layout_issue_dialog, null)
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("Input Issue Info")
            .setPositiveButton("Confirm") { _, _ ->
                val title = view.findViewById<EditText>(R.id.titleEditText).text.toString()
                val body = view.findViewById<EditText>(R.id.bodyEditText).text.toString()
                onInputListener.onInput(title, body)
            }
            .setNegativeButton("取消", null)
            .create()
    }

    interface OnInputListener {
        fun onInput(title: String, body: String)
    }
}