package com.mediaplus.app.ui.player

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import com.mediaplus.app.R
import com.mediaplus.app.data.repository.MediaRepository
import com.mediaplus.app.databinding.ActivityVideoPlayerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding
    private lateinit var player: ExoPlayer
    private val mediaRepository = MediaRepository()
    
    private var mediaItemId: String? = null
    private var playWhenReady = true
    private var currentPosition = 0L
    private var playbackStateReady = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get media item ID from intent
        mediaItemId = intent.getStringExtra("mediaItemId")
        
        if (mediaItemId == null) {
            finish()
            return
        }
        
        // Set up toolbar
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        // Initialize player
        initializePlayer()
        
        // Load media item
        loadMediaItem()
    }
    
    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()
        binding.videoPlayer.player = player
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    binding.videoPlayer.hideController()
                } else if (playbackState == Player.STATE_ENDED) {
                    finish()
                }
            }
        })
    }
    
    private fun loadMediaItem() {
        lifecycleScope.launch {
            val mediaItem = withContext(Dispatchers.IO) {
                mediaRepository.getMediaItemById(mediaItemId!!)
            }
            if (mediaItem != null) {
                binding.toolbar.title = mediaItem.title
                val dataSourceFactory = DefaultDataSource.Factory(this@VideoPlayerActivity)
                val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(mediaItem.uri)))
                player.setMediaSource(mediaSource)
                player.prepare()
                player.playWhenReady = playWhenReady
                player.seekTo(currentPosition)
            } else {
                finish()
            }
        }
    }
    
    override fun onStart() {
        super.onStart()
        initializePlayer()
        loadMediaItem()
    }
    
    override fun onResume() {
        super.onResume()
        hideSystemUi()
    }
    
    override fun onPause() {
        super.onPause()
        releasePlayer()
    }
    
    override fun onStop() {
        super.onStop()
        releasePlayer()
    }
    
    private fun releasePlayer() {
        if (::player.isInitialized) {
            playWhenReady = player.playWhenReady
            currentPosition = player.currentPosition
            playbackStateReady = player.playbackState == Player.STATE_READY
            player.release()
        }
    }
    
    private fun hideSystemUi() {
        binding.videoPlayer.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
}
