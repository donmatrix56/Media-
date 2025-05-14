package com.mediaplus.app.ui.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mediaplus.app.R
import com.mediaplus.app.data.model.Playlist
import com.mediaplus.app.databinding.ItemPlaylistBinding

class PlaylistsAdapter(
    private val onItemClick: (Playlist) -> Unit,
    private val onDeleteClick: (Playlist) -> Unit
) : ListAdapter<Playlist, PlaylistsAdapter.PlaylistViewHolder>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlaylistViewHolder(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.txtPlaylistName.text = playlist.name
            binding.txtPlaylistItemCount.text = playlist.description
                ?: binding.root.context.getString(R.string.playlist_no_description)

            // Load thumbnail if available, otherwise use default image
            Glide.with(binding.root.context)
                .load(playlist.thumbnailPath)
                .placeholder(R.drawable.ic_playlist)
                .error(R.drawable.ic_playlist)
                .into(binding.imgPlaylistThumbnail)

            // Set click listeners
            binding.root.setOnClickListener {
                onItemClick(playlist)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(playlist)
            }
        }
    }

    class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem == newItem
        }
    }
}
