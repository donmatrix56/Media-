package com.mediaplus.app.ui.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mediaplus.app.R

class SortOptionsDialog(
    context: Context,
    private val title: String,
    private val options: Array<String>,
    private val onOptionSelected: (String) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_sort_options)
          // Set dialog window background to use our custom shape with rounded corners
        window?.setBackgroundDrawableResource(R.drawable.bg_sort_dialog)
        
        // Set dialog title
        findViewById<TextView>(R.id.dialog_title).text = title
        
        // Set up RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_sort_options)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        // Set adapter
        recyclerView.adapter = SortOptionAdapter(options) { position ->
            onOptionSelected(options[position])
            dismiss()
        }
    }
}
