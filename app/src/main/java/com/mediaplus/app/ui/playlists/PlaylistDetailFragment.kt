package com.mediaplus.app.ui.playlists

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mediaplus.app.R
import com.mediaplus.app.data.model.MediaItem
import com.mediaplus.app.databinding.FragmentPlaylistDetailBinding
import com.mediaplus.app.ui.music.MusicAdapter
import com.mediaplus.app.ui.player.AudioPlayerActivity

class PlaylistDetailFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: PlaylistDetailFragmentArgs by navArgs()
    private lateinit var viewModel: PlaylistDetailViewModel
    private lateinit var mediaAdapter: MusicAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Create ViewModel with the playlist ID from navigation args
        val factory = PlaylistDetailViewModelFactory(args.playlistId)
        viewModel = ViewModelProvider(this, factory).get(PlaylistDetailViewModel::class.java)
        
        // Set up toolbar
        setupToolbar()
        
        // Set up RecyclerView
        setupRecyclerView()
        
        // Set up play button
        binding.btnPlay.setOnClickListener {
            playFirstTrack()
        }
        
        // Observe playlist data
        observePlaylistData()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupRecyclerView() {
        mediaAdapter = MusicAdapter(
            onItemClick = { mediaItem ->
                // Play the audio track
                playTrack(mediaItem)
            },
            onMenuClick = { mediaItem, view ->
                // Show options menu for media item
                showMediaItemOptionsMenu(mediaItem, view)
            }
        )
        
        binding.recyclerPlaylistItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mediaAdapter
        }
    }
    
    private fun observePlaylistData() {
        // Observe playlist info
        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            if (playlist != null) {
                // Update toolbar title
                binding.toolbar.title = playlist.name
                
                // Update playlist details
                binding.txtPlaylistName.text = playlist.name
                binding.txtPlaylistDescription.text = playlist.description ?: getString(R.string.playlist_no_description)
                
                // Load playlist thumbnail if available
                Glide.with(this)
                    .load(playlist.thumbnailPath)
                    .placeholder(R.drawable.ic_playlist)
                    .error(R.drawable.ic_playlist)
                    .into(binding.imgPlaylistCover)
            }
        }
        
        // Observe playlist media items
        viewModel.playlistItems.observe(viewLifecycleOwner) { mediaItems ->
            // Update the adapter with the media items
            mediaAdapter.submitList(mediaItems)
            
            // Update media count text
            val countText = resources.getQuantityString(
                R.plurals.media_count,
                mediaItems.size,
                mediaItems.size
            )
            binding.txtMediaCount.text = countText
            
            // Show/hide empty state
            if (mediaItems.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.btnPlay.visibility = View.GONE
            } else {
                binding.emptyState.visibility = View.GONE
                binding.btnPlay.visibility = View.VISIBLE
            }
        }
    }
    
    private fun playFirstTrack() {
        val items = viewModel.playlistItems.value
        if (!items.isNullOrEmpty()) {
            playTrack(items[0])
        }
    }
    
    private fun playTrack(mediaItem: MediaItem) {
        // Launch audio player activity with the media item ID and playlist ID
        val intent = Intent(requireContext(), AudioPlayerActivity::class.java).apply {
            putExtra("mediaItemId", mediaItem.id)
            putExtra("playlistId", args.playlistId)
        }
        startActivity(intent)
    }
    
    @Suppress("UNUSED_PARAMETER")
    private fun showMediaItemOptionsMenu(mediaItem: MediaItem, anchorView: View) {
        // TODO: Show a popup menu with options for the media item
        // Options: Remove from playlist, Play, Share, etc.
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
