package com.mediaplus.app.ui.videos

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mediaplus.app.R
import com.mediaplus.app.databinding.FragmentVideosBinding
import com.mediaplus.app.ui.player.VideoPlayerActivity
import com.mediaplus.app.data.model.MediaType
import com.mediaplus.app.utils.DimensionUtils
import com.mediaplus.app.utils.ResponsiveUIHelper

class VideosFragment : Fragment() {

    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: VideosViewModel
    private lateinit var videosAdapter: VideosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideosBinding.inflate(inflater, container, false)
        return binding.root
    }    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(VideosViewModel::class.java)
        
        // Apply responsive dimensions to UI
        ResponsiveUIHelper.getInstance(requireContext()).applyResponsiveDimensionsToLayout(binding.root)
        
        // Setup recycler view
        setupRecyclerView()
          // Observe data changes
        observeVideos()

        binding.btnSortVideos.setOnClickListener {
            val sortOptions = arrayOf("Date Created", "A-Z", "Size (Big to Small)")
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sort Videos")
                .setItems(sortOptions) { _, which ->
                    viewModel.sortVideos(sortOptions[which])
                }.create()
            dialog.show()
        }
    }
      private fun setupRecyclerView() {
        videosAdapter = VideosAdapter { mediaItem ->
            // Handle video item click
            val intent = Intent(requireContext(), VideoPlayerActivity::class.java).apply {
                putExtra("mediaItemId", mediaItem.id)
            }
            startActivity(intent)
        }        
        binding.recyclerVideos.apply {
            // Use the responsive column count from DimensionUtils instead of resources
            val columns = DimensionUtils.getInstance(requireContext()).getVideoGridColumns()
            layoutManager = GridLayoutManager(context, columns)
            adapter = videosAdapter
            
            // Apply responsive dimensions to the RecyclerView
            ResponsiveUIHelper.getInstance(requireContext()).setupResponsiveRecyclerView(this)
        }
    }
    
    private fun observeVideos() {
        viewModel.videos.observe(viewLifecycleOwner) { videos ->
            // Only show videos
            val filtered = videos.filter { it.mediaType == MediaType.VIDEO }
            videosAdapter.submitList(filtered)
            
            // Show/hide empty state
            if (filtered.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
            } else {
                binding.emptyState.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
