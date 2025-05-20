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
import com.mediaplus.app.utils.DimensionUtils
import com.mediaplus.app.utils.setupAsAudioListItem
import com.mediaplus.app.utils.setupWithCardAspectRatio
import java.util.concurrent.TimeUnit

class MediaAdapter(
    private val onItemClick: (MediaItem) -> Unit,    private val onRemoveClick: (MediaItem) -> Unit,
    private val isRecentAdapter: Boolean = false
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
    }    inner class ViewHolder(private val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Apply responsive dimensions to the item
            binding.root.setupWithCardAspectRatio()
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }fun bind(mediaItem: MediaItem) {
            binding.mediaTitle.text = mediaItem.title
            // Set subtitle based on media type
            when (mediaItem.mediaType) {
                MediaType.AUDIO -> binding.mediaSubtitle.text = mediaItem.artist ?: "Unknown Artist"
                MediaType.VIDEO -> binding.mediaSubtitle.text = formatDuration(mediaItem.duration)
                else -> binding.mediaSubtitle.text = ""
            }            // Set appropriate icon based on media type
            val iconRes = if (mediaItem.mediaType == MediaType.AUDIO) R.drawable.ic_music else R.drawable.ic_video
            
            // Set the media type icon in the top corner, but only for recent items
            binding.mediaTypeIcon.setImageResource(iconRes)
            binding.mediaTypeIcon.visibility = if (isRecentAdapter) android.view.View.VISIBLE else android.view.View.GONE

            // Check tuning flag for video thumbnail
            val showVideoThumbnail = binding.root.context.resources.getBoolean(
                binding.root.context.resources.getIdentifier(
                    "home_show_video_thumbnail",
                    "bool",
                    binding.root.context.packageName
                )
            )

            if (mediaItem.mediaType == MediaType.VIDEO) {
                if (showVideoThumbnail && !mediaItem.thumbnailPath.isNullOrBlank()) {
                    // Show the video thumbnail
                    binding.backgroundGradient.visibility = android.view.View.GONE
                    binding.mediaThumbnail.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                    Glide.with(binding.root.context)
                        .load(mediaItem.thumbnailPath)
                        .placeholder(iconRes)
                        .error(iconRes)
                        .centerCrop()
                        .into(binding.mediaThumbnail)                } else {
                    // Show icon only, no thumbnail
                    binding.backgroundGradient.visibility = android.view.View.VISIBLE
                    binding.mediaThumbnail.scaleType = android.widget.ImageView.ScaleType.CENTER
                    binding.mediaThumbnail.setImageResource(iconRes)
                    binding.mediaThumbnail.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN)
                }
                // Don't return here, continue to set up remove button for all items
            }

            // Background gradient visibility
            if (mediaItem.thumbnailPath.isNullOrBlank()) {
                binding.backgroundGradient.visibility = android.view.View.VISIBLE
                
                if (mediaItem.mediaType == MediaType.VIDEO && mediaItem.path.startsWith("content")) {
                    // For videos without thumbnails, extract a frame from the video
                    binding.mediaThumbnail.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                    binding.backgroundGradient.visibility = android.view.View.VISIBLE
                    
                    try {
                        val retriever = android.media.MediaMetadataRetriever()
                        retriever.setDataSource(binding.root.context, android.net.Uri.parse(mediaItem.uri))
                        
                        // Get a frame from the video (at 20% of the duration for a good representative frame)
                        val duration = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
                        val timeUs = (duration * 1000L / 5) // 20% of the way through
                        
                        val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
                            retriever.getScaledFrameAtTime(timeUs, android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC, 
                                                          binding.mediaThumbnail.width, binding.mediaThumbnail.height)
                        } else {
                            retriever.getFrameAtTime(timeUs, android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        }
                        
                        if (bitmap != null) {
                            binding.mediaThumbnail.setImageBitmap(bitmap)
                            retriever.release()
                        } else {
                            // Fall back to icon if frame extraction fails
                            binding.mediaThumbnail.setImageResource(iconRes)
                            binding.mediaThumbnail.clearColorFilter()
                        }
                    } catch (e: Exception) {
                        // On error, display the icon
                        binding.mediaThumbnail.scaleType = android.widget.ImageView.ScaleType.CENTER
                        binding.mediaThumbnail.setImageResource(iconRes)
                        binding.mediaThumbnail.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN)
                    }
                } else {
                    // For audio or non-video files without thumbnails, display icon
                    binding.mediaThumbnail.scaleType = android.widget.ImageView.ScaleType.CENTER
                    binding.mediaThumbnail.setImageResource(iconRes)
                    binding.mediaThumbnail.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN)
                }            } else {                binding.backgroundGradient.visibility = android.view.View.GONE
                binding.mediaThumbnail.clearColorFilter()
                  // Load thumbnail
                Glide.with(binding.root.context)
                    .load(mediaItem.thumbnailPath)
                    .placeholder(iconRes)
                    .error(iconRes)
                    .apply { if (mediaItem.mediaType == MediaType.AUDIO) fitCenter() else centerCrop() }
                    .into(binding.mediaThumbnail)            }
            
            // Configure remove button - ensure it's visible for all media types in Recent Files
            binding.mediaRemove.apply {
                // Make remove button more prominent and always visible for recent items
                if (isRecentAdapter) {
                    visibility = android.view.View.VISIBLE
                    alpha = 0.85f
                    
                    // Apply a nice animation when showing the remove button
                    if (this.animation == null) {
                        val fadeIn = android.view.animation.AlphaAnimation(0.0f, 1.0f)
                        fadeIn.duration = 300
                        this.startAnimation(fadeIn)
                    }
                } else {
                    visibility = android.view.View.GONE
                }
                
                setOnClickListener {
                    onRemoveClick(mediaItem)
                }
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
