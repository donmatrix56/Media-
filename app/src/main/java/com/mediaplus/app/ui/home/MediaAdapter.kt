package com.mediaplus.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mediaplus.app.R
import com.mediaplus.app.data.model.MediaItem
import com.mediaplus.app.data.model.MediaType
import com.mediaplus.app.databinding.ItemMediaBinding
import java.util.concurrent.TimeUnit

class MediaAdapter(
    private val onItemClick: (MediaItem) -> Unit,
    private val onRemoveClick: (MediaItem) -> Unit
) : ListAdapter<MediaItem, MediaAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMediaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(mediaItem: MediaItem) {
            binding.mediaTitle.text = mediaItem.title
            
            // Set subtitle based on media type
            when (mediaItem.mediaType) {
                MediaType.AUDIO -> binding.mediaSubtitle.text = mediaItem.artist ?: "Unknown Artist"
                MediaType.VIDEO -> binding.mediaSubtitle.text = formatDuration(mediaItem.duration)
                else -> binding.mediaSubtitle.text = ""
            }
            
            // Set appropriate icon based on media type
            val iconRes = if (mediaItem.mediaType == MediaType.AUDIO) R.drawable.ic_music else R.drawable.ic_video
            
            // Load thumbnail
            Glide.with(binding.root.context)
                .load(mediaItem.thumbnailPath ?: mediaItem.uri)
                .placeholder(iconRes)
                .error(iconRes)
                .apply { if (mediaItem.mediaType == MediaType.AUDIO) fitCenter() else centerCrop() }
                .into(binding.mediaThumbnail)

            // Set remove click listener
            binding.mediaRemove.setOnClickListener {
                onRemoveClick(mediaItem)
            }
        }
        
        private fun formatDuration(durationMs: Long): String {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) - 
                    TimeUnit.MINUTES.toSeconds(minutes)
            return String.format("%02d:%02d", minutes, seconds)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MediaItem>() {
            override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
