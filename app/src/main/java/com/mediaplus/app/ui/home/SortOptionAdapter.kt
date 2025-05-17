package com.mediaplus.app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mediaplus.app.R

class SortOptionAdapter(
    private val options: Array<String>,
    private val onOptionSelected: (Int) -> Unit
) : RecyclerView.Adapter<SortOptionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon_sort_type)
        val text: TextView = itemView.findViewById(R.id.text_sort_option)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sort_option, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options[position]
        holder.text.text = option
        
        // Set appropriate icon based on sort option
        val iconRes = when (option) {
            "Date Created" -> R.drawable.ic_sort_date
            "Most Played" -> R.drawable.ic_sort_played
            "A-Z" -> R.drawable.ic_sort_az
            "Size (Big to Small)" -> R.drawable.ic_sort_size
            else -> R.drawable.ic_filter_list
        }
        holder.icon.setImageResource(iconRes)
        
        // Set click listener
        holder.itemView.setOnClickListener {
            onOptionSelected(position)
        }
    }

    override fun getItemCount(): Int = options.size
}
