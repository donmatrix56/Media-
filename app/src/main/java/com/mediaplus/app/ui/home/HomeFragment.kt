package com.mediaplus.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mediaplus.app.databinding.FragmentHomeBinding
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.mediaplus.app.data.model.MediaType
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.animation.ObjectAnimator
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mediaplus.app.R

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: HomeViewModel

    private val pickVideoLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            requireContext().contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.addMediaFromUri(requireContext(), it, MediaType.VIDEO)
        }
    }
    private val pickAudioLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            requireContext().contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.addMediaFromUri(requireContext(), it, MediaType.AUDIO)
        }
    }

    private var isSearchVisible = false
    private var searchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Index media on first launch
        if (isFirstLaunch()) {
            viewModel.scanMediaFiles(requireContext())
            setFirstLaunchDone()
        }
        
        // Setup recyclerview adapters
        setupRecentAdapter()
        setupVideoAdapter()
        setupMusicAdapter()
        
        // --- Sort and Search Logic ---
        val sortOptions = arrayOf("Date Created", "Most Played", "A-Z", "Size (Big to Small)")
        val sortColors = ContextCompat.getColor(requireContext(), R.color.primary)
        // Recent
        binding.btnSortRecent.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sort Recent Files")
                .setItems(sortOptions) { _, which ->
                    viewModel.sortRecent(sortOptions[which])
                }.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(sortColors)
        }
        binding.btnSearchRecent.setOnClickListener {
            val edit = binding.editSearchRecent
            edit.visibility = View.VISIBLE
            edit.requestFocus()
            edit.setSelection(edit.text?.length ?: 0)
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT)
        }
        binding.editSearchRecent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // TODO: Filter recent list by text
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        // Videos
        binding.btnSortVideos.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sort Videos")
                .setItems(sortOptions) { _, which ->
                    viewModel.sortVideos(sortOptions[which])
                }.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(sortColors)
        }
        binding.btnSearchVideos.setOnClickListener {
            val edit = binding.editSearchVideos
            edit.visibility = View.VISIBLE
            edit.requestFocus()
            edit.setSelection(edit.text?.length ?: 0)
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT)
        }
        binding.editSearchVideos.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // TODO: Filter videos list by text
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        // Music
        binding.btnSortMusic.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sort Audio")
                .setItems(sortOptions) { _, which ->
                    viewModel.sortMusic(sortOptions[which])
                }.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(sortColors)
        }
        binding.btnSearchMusic.setOnClickListener {
            val edit = binding.editSearchMusic
            edit.visibility = View.VISIBLE
            edit.requestFocus()
            edit.setSelection(edit.text?.length ?: 0)
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT)
        }
        binding.editSearchMusic.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // TODO: Filter music list by text
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Observe sorted lists
        viewModel.sortedRecent.observe(viewLifecycleOwner) { items ->
            (binding.recyclerRecent.adapter as MediaAdapter).submitList(items.take(10))
        }
        viewModel.sortedVideos.observe(viewLifecycleOwner) { items ->
            (binding.recyclerVideos.adapter as MediaAdapter).submitList(items.take(10))
        }
        viewModel.sortedMusic.observe(viewLifecycleOwner) { items ->
            (binding.recyclerMusic.adapter as MediaAdapter).submitList(items.take(10))
        }
        // Initial sort
        viewModel.sortRecent("Date Created")
        viewModel.sortVideos("Date Created")
        viewModel.sortMusic("Date Created")
    }

    private fun isFirstLaunch(): Boolean {
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("first_launch", true)
    }

    private fun setFirstLaunchDone() {
        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("first_launch", false).apply()
    }

    private fun setupRecentAdapter() {
        val recentAdapter = MediaAdapter(
            onItemClick = { mediaItem ->
                // Handle click based on media type
                when (mediaItem.mediaType) {
                    com.mediaplus.app.data.model.MediaType.AUDIO -> {
                        val intent = android.content.Intent(requireContext(), com.mediaplus.app.ui.player.AudioPlayerActivity::class.java).apply {
                            putExtra("mediaItemId", mediaItem.id)
                        }
                        startActivity(intent)
                    }
                    com.mediaplus.app.data.model.MediaType.VIDEO -> {
                        val intent = android.content.Intent(requireContext(), com.mediaplus.app.ui.player.VideoPlayerActivity::class.java).apply {
                            putExtra("mediaItemId", mediaItem.id)
                        }
                        startActivity(intent)
                    }
                    com.mediaplus.app.data.model.MediaType.IMAGE -> {
                        // TODO: Handle image type if needed
                    }
                }
            },
            onRemoveClick = { mediaItem ->
                viewModel.removeFromRecent(mediaItem)
            }
        )
        
        binding.recyclerRecent.adapter = recentAdapter
        binding.recyclerRecent.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }
    
    private fun setupVideoAdapter() {
        val videoAdapter = MediaAdapter(
            onItemClick = { mediaItem ->
                // Open video player
                val intent = android.content.Intent(requireContext(), com.mediaplus.app.ui.player.VideoPlayerActivity::class.java).apply {
                    putExtra("mediaItemId", mediaItem.id)
                }
                startActivity(intent)
            },
            onRemoveClick = { mediaItem ->
                viewModel.deleteMediaItem(mediaItem)
            }
        )
        
        binding.recyclerVideos.adapter = videoAdapter
        binding.recyclerVideos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }
    
    private fun setupMusicAdapter() {
        val musicAdapter = MediaAdapter(
            onItemClick = { mediaItem ->
                // Open audio player
                val intent = android.content.Intent(requireContext(), com.mediaplus.app.ui.player.AudioPlayerActivity::class.java).apply {
                    putExtra("mediaItemId", mediaItem.id)
                }
                startActivity(intent)
            },
            onRemoveClick = { mediaItem ->
                viewModel.deleteMediaItem(mediaItem)
            }
        )
        
        binding.recyclerMusic.adapter = musicAdapter
        binding.recyclerMusic.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_PICK_VIDEO = 101
        private const val REQUEST_PICK_AUDIO = 102
    }
}
