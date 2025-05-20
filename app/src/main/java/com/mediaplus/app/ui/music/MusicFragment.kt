package com.mediaplus.app.ui.music

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mediaplus.app.R
import com.mediaplus.app.data.model.MediaType
import com.mediaplus.app.databinding.FragmentMusicBinding
import com.mediaplus.app.ui.player.AudioPlayerActivity

class MusicFragment : Fragment() {

    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: MusicViewModel
    private lateinit var musicAdapter: MusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(MusicViewModel::class.java)
        
        // Setup recycler view
        setupRecyclerView()
          // Observe data changes
        observeMusic()

        binding.btnSortMusic.setOnClickListener {
            val sortOptions = arrayOf("Date Created", "A-Z", "Size (Big to Small)")
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sort Audio")
                .setItems(sortOptions) { _, which ->
                    viewModel.sortMusic(sortOptions[which])
                }.create()
            dialog.show()
        }
    }
    
    private fun setupRecyclerView() {
        musicAdapter = MusicAdapter(
            onItemClick = { mediaItem ->
                // Play the audio
                val intent = Intent(requireContext(), AudioPlayerActivity::class.java).apply {
                    putExtra("mediaItemId", mediaItem.id)
                }
                startActivity(intent)
            },
            onMenuClick = { mediaItem, view ->
                // Show options menu
                showOptionsMenu(mediaItem, view)
            }
        )
        binding.recyclerMusic.apply {
            // Use the responsive column count from DimensionUtils
            val columns = com.mediaplus.app.utils.DimensionUtils.getInstance(requireContext()).getAudioGridColumns()
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, columns)
            adapter = musicAdapter
        }
    }
    
    private fun observeMusic() {
        viewModel.music.observe(viewLifecycleOwner) { music ->
            // Only show audio
            val filtered = music.filter { it.mediaType == MediaType.AUDIO }
            musicAdapter.submitList(filtered)
            
            // Show/hide empty state
            if (filtered.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
            } else {
                binding.emptyState.visibility = View.GONE
            }
        }
    }
    
    private fun showOptionsMenu(mediaItem: com.mediaplus.app.data.model.MediaItem, view: View) {
        val popupMenu = android.widget.PopupMenu(requireContext(), view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(com.mediaplus.app.R.menu.menu_music_options, popupMenu.menu)
        
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                com.mediaplus.app.R.id.action_add_to_playlist -> {
                    showAddToPlaylistDialog(mediaItem)
                    true
                }
                com.mediaplus.app.R.id.action_share -> {
                    shareMediaItem(mediaItem)
                    true
                }
                else -> false
            }
        }
        
        popupMenu.show()
    }
    
    private fun showAddToPlaylistDialog(mediaItem: com.mediaplus.app.data.model.MediaItem) {
        val dialogBinding = com.mediaplus.app.databinding.DialogAddToPlaylistBinding.inflate(layoutInflater)
        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()
        
        // Create adapter
        val adapter = com.mediaplus.app.ui.playlists.SelectPlaylistAdapter { playlist ->
            // Add media item to selected playlist
            viewModel.addToPlaylist(mediaItem.id, playlist.id)
            dialog.dismiss()
            
            // Show confirmation
            com.google.android.material.snackbar.Snackbar.make(
                binding.root,
                getString(com.mediaplus.app.R.string.msg_added_to_playlist, mediaItem.title, playlist.name),
                com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
            ).show()
        }
        
        // Set up RecyclerView
        dialogBinding.recyclerPlaylists.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
        
        // Observe playlists
        val playlistViewModel = ViewModelProvider(this).get(com.mediaplus.app.ui.playlists.PlaylistsViewModel::class.java)
        playlistViewModel.playlists.observe(this) { playlists ->
            if (playlists.isEmpty()) {
                dialogBinding.txtNoPlaylists.visibility = View.VISIBLE
                dialogBinding.recyclerPlaylists.visibility = View.GONE
            } else {
                dialogBinding.txtNoPlaylists.visibility = View.GONE
                dialogBinding.recyclerPlaylists.visibility = View.VISIBLE
                adapter.submitList(playlists)
            }
        }
        
        // Create new playlist button
        dialogBinding.btnCreatePlaylist.setOnClickListener {
            dialog.dismiss()
            showCreatePlaylistWithMediaDialog(mediaItem)
        }
        
        dialog.show()
    }
    
    private fun showCreatePlaylistWithMediaDialog(mediaItem: com.mediaplus.app.data.model.MediaItem) {
        val dialogBinding = com.mediaplus.app.databinding.DialogCreatePlaylistBinding.inflate(layoutInflater)
        
        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle(com.mediaplus.app.R.string.dialog_create_playlist)
            .setView(dialogBinding.root)
            .setPositiveButton(com.mediaplus.app.R.string.dialog_save, null) // Set null, we'll set the listener ourselves
            .setNegativeButton(com.mediaplus.app.R.string.dialog_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val playlistName = dialogBinding.editPlaylistName.text.toString().trim()
                val playlistDescription = dialogBinding.editPlaylistDescription.text.toString().trim()
                
                if (playlistName.isNotEmpty()) {
                    // Create the playlist and add the media item
                    viewModel.createPlaylistAndAddMedia(playlistName, playlistDescription, mediaItem.id)
                    dialog.dismiss()
                    
                    // Show confirmation
                    com.google.android.material.snackbar.Snackbar.make(
                        binding.root,
                        getString(com.mediaplus.app.R.string.msg_created_playlist_with_media, playlistName, mediaItem.title),
                        com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    dialogBinding.editPlaylistName.error = "Playlist name cannot be empty"
                }
            }
        }
        
        dialog.show()
    }
    
    private fun shareMediaItem(mediaItem: com.mediaplus.app.data.model.MediaItem) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "${mediaItem.title} - ${mediaItem.artist ?: "Unknown Artist"}")
            putExtra(Intent.EXTRA_SUBJECT, "Check out this song")
        }
        startActivity(Intent.createChooser(shareIntent, getString(com.mediaplus.app.R.string.action_share)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
