package com.mediaplus.app.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.mediaplus.app.R
import com.mediaplus.app.databinding.FragmentPlaylistsBinding
import com.mediaplus.app.databinding.DialogCreatePlaylistBinding

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: PlaylistsViewModel
    private lateinit var playlistsAdapter: PlaylistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(PlaylistsViewModel::class.java)
        
        // Setup recycler view
        setupRecyclerView()
        
        // Setup FAB for creating new playlists
        binding.fabCreatePlaylist.setOnClickListener {
            showCreatePlaylistDialog()
        }
        
        // Observe playlists
        observePlaylists()
    }
    
    private fun setupRecyclerView() {
        playlistsAdapter = PlaylistsAdapter(
            onItemClick = { playlist ->
                // Navigate to playlist detail
                val bundle = android.os.Bundle().apply { putLong("playlistId", playlist.id) }
                findNavController().navigate(R.id.playlistDetailFragment, bundle)
            },
            onDeleteClick = { playlist ->
                // Show delete confirmation
                showDeleteConfirmationDialog(playlist.id, playlist.name)
            }
        )
        
        binding.recyclerPlaylists.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playlistsAdapter
        }
    }
    
    private fun observePlaylists() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistsAdapter.submitList(playlists)
            
            // Show/hide empty state
            if (playlists.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
            } else {
                binding.emptyState.visibility = View.GONE
            }
        }
    }
    
    private fun showCreatePlaylistDialog() {
        val dialogBinding = DialogCreatePlaylistBinding.inflate(layoutInflater)
        
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_create_playlist)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.dialog_save, null) // Set null, we'll set the listener ourselves
            .setNegativeButton(R.string.dialog_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val playlistName = dialogBinding.editPlaylistName.text.toString().trim()
                val playlistDescription = dialogBinding.editPlaylistDescription.text.toString().trim()
                
                if (playlistName.isNotEmpty()) {
                    viewModel.createPlaylist(playlistName, playlistDescription)
                    dialog.dismiss()
                } else {
                    dialogBinding.editPlaylistName.error = "Playlist name cannot be empty"
                }
            }
        }
        
        dialog.show()
    }
    
    private fun showDeleteConfirmationDialog(playlistId: Long, playlistName: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Playlist")
            .setMessage("Are you sure you want to delete \"$playlistName\"?")
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                viewModel.deletePlaylist(playlistId)
            }
            .setNegativeButton(R.string.dialog_no, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
