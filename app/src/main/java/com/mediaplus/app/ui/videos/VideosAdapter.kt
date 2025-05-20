package com.mediaplus.app.ui.videos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mediaplus.app.R
import com.mediaplus.app.data.model.MediaItem
import com.mediaplus.app.databinding.ItemVideoBinding
import com.mediaplus.app.utils.DimensionUtils
import com.mediaplus.app.utils.setAdaptiveCornerRadius
import com.mediaplus.app.utils.setAdaptiveTextSize
import com.mediaplus.app.utils.setupAsVideoGridItem
import java.util.concurrent.TimeUnit

class VideosAdapter(
    private val onItemClick: (MediaItem) -> Unit
) : ListAdapter<MediaItem, VideosAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }    inner class ViewHolder(
        private val binding: ItemVideoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Apply responsive dimensions to the item
            binding.root.setupAsVideoGridItem()
            
            // Apply adaptive text sizes
            binding.videoTitle.setAdaptiveTextSize(12f, 16f)
            binding.videoDuration.setAdaptiveTextSize(10f, 14f)
            
            // Apply adaptive corner radius
            binding.root.setAdaptiveCornerRadius(8f)
            
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(mediaItem: MediaItem) {
            binding.videoTitle.text = mediaItem.title
            binding.videoDuration.text = formatDuration(mediaItem.duration)            // Load thumbnail if available, otherwise show icon
            if (!mediaItem.thumbnailPath.isNullOrBlank()) {
                Glide.with(binding.root.context)
                    .load(mediaItem.thumbnailPath)
                    .placeholder(R.drawable.ic_video)
                    .error(R.drawable.ic_video)
                    .centerCrop()
                    .into(binding.videoThumbnail)
            } else {
                // If no thumbnail, show icon
                binding.videoThumbnail.setImageResource(R.drawable.ic_video)
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
