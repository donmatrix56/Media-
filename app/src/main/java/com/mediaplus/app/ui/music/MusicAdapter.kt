package com.mediaplus.app.ui.music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mediaplus.app.R
import com.mediaplus.app.data.model.MediaItem
import com.mediaplus.app.databinding.ItemAudioBinding
import java.util.concurrent.TimeUnit

class MusicAdapter(
    private val onItemClick: (MediaItem) -> Unit,
    private val onMenuClick: (MediaItem, View) -> Unit
) : ListAdapter<MediaItem, MusicAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAudioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemAudioBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
            
            binding.audioMenu.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMenuClick(getItem(position), it)
                }
            }
        }

        fun bind(mediaItem: MediaItem) {
            binding.audioTitle.text = mediaItem.title
            binding.audioArtist.text = mediaItem.artist ?: "Unknown Artist"
            binding.audioDuration.text = formatDuration(mediaItem.duration)
            
            // Load album art
            Glide.with(binding.root.context)
                .load(mediaItem.thumbnailPath)
                .placeholder(R.drawable.ic_music)
                .error(R.drawable.ic_music)
                .centerCrop()
                .into(binding.audioThumbnail)
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
