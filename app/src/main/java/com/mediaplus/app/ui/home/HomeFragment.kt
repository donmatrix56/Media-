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
import android.content.Context
import android.animation.ObjectAnimator
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import com.mediaplus.app.R
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

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
    }    // Search functionality removed
    private val requestVideoPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.scanVideoFiles(requireContext())
        } else {
            android.widget.Toast.makeText(requireContext(), "Permission denied. Cannot scan videos.", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    
    private val requestAudioPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.scanAudioFiles(requireContext())
        } else {
            android.widget.Toast.makeText(requireContext(), "Permission denied. Cannot scan audio.", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

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
        
        // Recent
        binding.btnSortRecent.setOnClickListener {
            val dialog = SortOptionsDialog(
                requireContext(),
                "Sort Recent Files",
                sortOptions
            ) { selectedOption ->
                viewModel.sortRecent(selectedOption)
            }
            dialog.show()
        }
          // Videos
        binding.btnSortVideos.setOnClickListener {
            val dialog = SortOptionsDialog(
                requireContext(),
                "Sort Videos",
                sortOptions
            ) { selectedOption ->
                viewModel.sortVideos(selectedOption)
            }
            dialog.show()
        }          // Scan videos button
        binding.root.findViewById<View?>(R.id.btn_refresh_videos)?.let { btn ->
            btn.setOnClickListener {
                animateRefreshButton(btn)
                if (hasVideoScanPermission()) {
                    viewModel.scanVideoFiles(requireContext())
                } else {
                    if (shouldShowRequestPermissionRationale(getVideoScanPermission())) {
                        androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Permission Required")
                            .setMessage("Media+ needs permission to access your videos in order to scan and display them.")
                            .setPositiveButton("Allow") { _, _ ->
                                requestVideoPermissionLauncher.launch(getVideoScanPermission())
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    } else {
                        requestVideoPermissionLauncher.launch(getVideoScanPermission())
                    }
                }
            }
        }
          // Music
        binding.btnSortMusic.setOnClickListener {
            val dialog = SortOptionsDialog(
                requireContext(),
                "Sort Audio",
                sortOptions
            ) { selectedOption ->
                viewModel.sortMusic(selectedOption)
            }
            dialog.show()
        }
          // Scan audio button
        binding.root.findViewById<View?>(R.id.btn_refresh_music)?.let { btn ->
            btn.setOnClickListener {
                animateRefreshButton(btn)
                if (hasAudioScanPermission()) {
                    viewModel.scanAudioFiles(requireContext())
                } else {
                    if (shouldShowRequestPermissionRationale(getAudioScanPermission())) {
                        androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Permission Required")
                            .setMessage("Media+ needs permission to access your audio files in order to scan and display them.")
                            .setPositiveButton("Allow") { _, _ ->
                                requestAudioPermissionLauncher.launch(getAudioScanPermission())
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    } else {
                        requestAudioPermissionLauncher.launch(getAudioScanPermission())
                    }
                }
            }
        }

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
    }    private fun setupRecentAdapter() {
        val tuning = com.mediaplus.app.utils.TuningProvider.getInstance(requireContext())
        val cardWidth = tuning.getHomeCardWidth().toInt().takeIf { it > 0 } ?: ((resources.displayMetrics.widthPixels - 2 * tuning.getHomeCardHorizontalPadding().toInt() - 2 * tuning.getHomeCardHorizontalSpacing().toInt()) / 3)
        val minWidth = tuning.getHomeCardMinWidth().toInt()
        val maxWidth = tuning.getHomeCardMaxWidth().toInt()
        val spacing = tuning.getHomeCardHorizontalSpacing().toInt()
        val padding = tuning.getHomeCardHorizontalPadding().toInt()

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
            },
            isRecentAdapter = true
        )
        binding.recyclerRecent.adapter = recentAdapter
        binding.recyclerRecent.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext(), androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerRecent.setPadding(padding, 0, padding, 0)
        binding.recyclerRecent.clipToPadding = false
        binding.recyclerRecent.addItemDecoration(object : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: android.graphics.Rect, view: android.view.View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
                outRect.right = spacing
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.left = spacing
                }
                // Set card width - ensure all values are Int
                val width = kotlin.math.max(minWidth, kotlin.math.min(cardWidth, maxWidth))
                view.layoutParams.width = width
            }
        })
    }
    
    private fun setupVideoAdapter() {
        val tuning = com.mediaplus.app.utils.TuningProvider.getInstance(requireContext())
        val cardWidth = tuning.getHomeCardWidth().toInt().takeIf { it > 0 } ?: ((resources.displayMetrics.widthPixels - 2 * tuning.getHomeCardHorizontalPadding().toInt() - 2 * tuning.getHomeCardHorizontalSpacing().toInt()) / 3)
        val minWidth = tuning.getHomeCardMinWidth().toInt()
        val maxWidth = tuning.getHomeCardMaxWidth().toInt()
        val spacing = tuning.getHomeCardHorizontalSpacing().toInt()
        val padding = tuning.getHomeCardHorizontalPadding().toInt()

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
        binding.recyclerVideos.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext(), androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerVideos.setPadding(padding, 0, padding, 0)
        binding.recyclerVideos.clipToPadding = false
        binding.recyclerVideos.addItemDecoration(object : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: android.graphics.Rect, view: android.view.View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
                outRect.right = spacing
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.left = spacing
                }
                // Set card width - ensure all values are Int
                val width = kotlin.math.max(minWidth, kotlin.math.min(cardWidth, maxWidth))
                view.layoutParams.width = width
            }
        })
    }
    
    private fun setupMusicAdapter() {
        val tuning = com.mediaplus.app.utils.TuningProvider.getInstance(requireContext())
        val cardWidth = tuning.getHomeCardWidth().toInt().takeIf { it > 0 } ?: ((resources.displayMetrics.widthPixels - 2 * tuning.getHomeCardHorizontalPadding().toInt() - 2 * tuning.getHomeCardHorizontalSpacing().toInt()) / 3)
        val minWidth = tuning.getHomeCardMinWidth().toInt()
        val maxWidth = tuning.getHomeCardMaxWidth().toInt()
        val spacing = tuning.getHomeCardHorizontalSpacing().toInt()
        val padding = tuning.getHomeCardHorizontalPadding().toInt()

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
        binding.recyclerMusic.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext(), androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerMusic.setPadding(padding, 0, padding, 0)
        binding.recyclerMusic.clipToPadding = false
        binding.recyclerMusic.addItemDecoration(object : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: android.graphics.Rect, view: android.view.View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
                outRect.right = spacing
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.left = spacing
                }
                // Set card width - ensure all values are Int
                val width = kotlin.math.max(minWidth, kotlin.math.min(cardWidth, maxWidth))
                view.layoutParams.width = width
            }
        })
    }    // Animate the refresh button during scanning with a modern animation
    private fun animateRefreshButton(button: View) {
        // Disable the button during scanning
        button.isEnabled = false
        
        // Ensure the pivot point is set correctly to the center of the button
        button.pivotX = button.width / 2f
        button.pivotY = button.height / 2f
        
        // Create a set of animations for a more dynamic, modern effect
        val animatorSet = android.animation.AnimatorSet()
        
        // Initial pulse animation
        val scaleUpX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.2f)
        val scaleUpY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.2f)
        val scaleDownX = ObjectAnimator.ofFloat(button, "scaleX", 1.2f, 0.9f)
        val scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", 1.2f, 0.9f)
        val scaleNormalX = ObjectAnimator.ofFloat(button, "scaleX", 0.9f, 1f)
        val scaleNormalY = ObjectAnimator.ofFloat(button, "scaleY", 0.9f, 1f)
        
        // Pulse before rotation
        val pulseSet = android.animation.AnimatorSet()
        pulseSet.playSequentially(
            android.animation.AnimatorSet().apply { playTogether(scaleUpX, scaleUpY) },
            android.animation.AnimatorSet().apply { playTogether(scaleDownX, scaleDownY) },
            android.animation.AnimatorSet().apply { playTogether(scaleNormalX, scaleNormalY) }
        )
        pulseSet.duration = 300
        
        // Create rotation animation with easing
        val rotateAnimation = ObjectAnimator.ofFloat(button, "rotation", 0f, 360f)
        rotateAnimation.duration = 1200
        rotateAnimation.repeatCount = ObjectAnimator.INFINITE
        rotateAnimation.interpolator = android.view.animation.AccelerateDecelerateInterpolator()        // Add a subtle alpha pulsing effect
        val fadeOut = ObjectAnimator.ofFloat(button, "alpha", 1f, 0.7f)
        fadeOut.duration = 600
        fadeOut.repeatCount = ObjectAnimator.INFINITE
        fadeOut.repeatMode = ObjectAnimator.REVERSE
        
        // Add all animations to the set - run pulse once, then start continuous rotation
        animatorSet.play(pulseSet).before(rotateAnimation)
        animatorSet.play(rotateAnimation).with(fadeOut)
        animatorSet.start()
        
        // Create a circular reveal effect for visual feedback
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val cx = button.width / 2
            val cy = button.height / 2
            val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
            val anim = android.view.ViewAnimationUtils.createCircularReveal(button, cx, cy, 0f, finalRadius)
            anim.duration = 300
            anim.start()
        }
        
        // Stop animation after 2.5 seconds (typical scan time)
        button.postDelayed({
            animatorSet.cancel()
            
            // Reset to initial state with a smooth animation
            val resetAnimation = android.animation.AnimatorSet()
            val resetRotate = ObjectAnimator.ofFloat(button, "rotation", button.rotation, 0f)
            val resetAlpha = ObjectAnimator.ofFloat(button, "alpha", button.alpha, 1f)
            resetAnimation.playTogether(resetRotate, resetAlpha)
            resetAnimation.duration = 300
            resetAnimation.start()
            
            button.isEnabled = true
            
            // Get appropriate message based on button id
            val message = when (button.id) {
                R.id.btn_refresh_videos -> "Video scan completed"
                R.id.btn_refresh_music -> "Audio scan completed"
                else -> "Scan completed"
            }
            // Show toast message
            android.widget.Toast.makeText(
                requireContext(),
                message,
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }, 2500)
    }
    
    private fun hasVideoScanPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }    private fun getVideoScanPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_VIDEO
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }
    
    private fun hasAudioScanPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun getAudioScanPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_AUDIO
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
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
