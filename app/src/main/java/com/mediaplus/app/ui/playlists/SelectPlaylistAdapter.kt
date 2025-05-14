package com.mediaplus.app.ui.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mediaplus.app.R
import com.mediaplus.app.data.model.Playlist
import com.mediaplus.app.databinding.ItemSelectPlaylistBinding

class SelectPlaylistAdapter(
    private val onPlaylistSelected: (Playlist) -> Unit
) : ListAdapter<Playlist, SelectPlaylistAdapter.ViewHolder>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSelectPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemSelectPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPlaylistSelected(getItem(position))
                }
            }
        }

        fun bind(playlist: Playlist) {
            binding.txtPlaylistName.text = playlist.name

            // Load thumbnail if available, otherwise use default image
            Glide.with(binding.root.context)
                .load(playlist.thumbnailPath)
                .placeholder(R.drawable.ic_playlist)
                .error(R.drawable.ic_playlist)
                .into(binding.imgPlaylistThumbnail)
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
