package com.mediaplus.app.ui.player

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.common.Player
import com.mediaplus.app.R
import com.mediaplus.app.data.repository.MediaRepository
import com.mediaplus.app.databinding.ActivityAudioPlayerBinding
import com.mediaplus.app.util.AlbumArtFetcher
import android.graphics.drawable.BitmapDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.widget.Toast

@Suppress("DEPRECATION")
class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var player: ExoPlayer
    private val mediaRepository = MediaRepository()
    
    private var mediaItemId: String? = null
    private var playlistId: Long = -1L
    private var playWhenReady = true
    private var currentPosition = 0L
    private var playbackStateReady = true
    private var isUserSeeking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get media item ID and playlist ID from intent
        mediaItemId = intent.getStringExtra("mediaItemId")
        playlistId = intent.getLongExtra("playlistId", -1L)
        
        if (mediaItemId == null) {
            finish()
            return
        }
        
        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        // Set up player controls
        setupPlayerControls()
        
        // Initialize player
        initializePlayer()
        
        // Load media item
        loadMediaItem()
    }
    
    private fun setupPlayerControls() {
        binding.btnPlay.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
                binding.btnPlay.setImageResource(R.drawable.ic_play)
            } else {
                player.play()
                binding.btnPlay.setImageResource(R.drawable.ic_pause)
            }
        }
        binding.btnPrevious.setOnClickListener {
            // Play previous track
            playPreviousTrack()
        }
        
        binding.btnNext.setOnClickListener {
            // Play next track
            playNextTrack()
        }
        
        binding.btnShuffle.setOnClickListener {
            // Toggle shuffle mode
            toggleShuffleMode()
        }
        
        binding.btnRepeat.setOnClickListener {
            // Toggle repeat mode
            toggleRepeatMode()
        }

        binding.btnEqualizer.setOnClickListener {
            try {
                val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, player.audioSessionId)
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName)
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No equalizer available on this device", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Unable to launch equalizer", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLyrics.setOnClickListener {
            val artist = binding.txtArtist.text.toString()
            val title = binding.txtTitle.text.toString()
            fetchAndShowLyrics(artist, title)
        }
    }
    
    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()
        // No PlayerView in audio layout, so do not assign player to a view

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        binding.btnPlay.setImageResource(R.drawable.ic_pause)
                        binding.seekBar.max = player.duration.toInt()
                        // Start seekbar updates
                        startSeekBarUpdates()
                    }
                    Player.STATE_BUFFERING -> {
                        binding.progressBar.visibility = android.view.View.VISIBLE
                    }
                    Player.STATE_ENDED -> {
                        binding.btnPlay.setImageResource(R.drawable.ic_play)
                    }
                    Player.STATE_IDLE -> {
                        binding.progressBar.visibility = android.view.View.GONE
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    binding.btnPlay.setImageResource(R.drawable.ic_pause)
                } else {
                    binding.btnPlay.setImageResource(R.drawable.ic_play)
                }
            }
        })
    }
    
    private fun startSeekBarUpdates() {
        binding.seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player.seekTo(progress.toLong())
                }
                
                // Update time labels
                binding.txtCurrentTime.text = formatTime(player.currentPosition)
                binding.txtTotalTime.text = formatTime(player.duration)
            }
            
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {
                isUserSeeking = true
            }
            
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                isUserSeeking = false
                seekBar?.let { player.seekTo(it.progress.toLong()) }
            }
        })
        
        // Update seekbar periodically
        lifecycleScope.launch {
            while (true) {
                withContext(Dispatchers.Main) {
                    if (player.isPlaying && !isUserSeeking) {
                        binding.seekBar.progress = player.currentPosition.toInt()
                        binding.txtCurrentTime.text = formatTime(player.currentPosition)
                    }
                }
                kotlinx.coroutines.delay(1000)
            }
        }
    }
    
    private fun formatTime(timeMs: Long): String {
        val totalSeconds = timeMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    private fun loadMediaItem() {
        lifecycleScope.launch {
            val mediaItem = withContext(Dispatchers.IO) {
                mediaRepository.getMediaItemById(mediaItemId!!)
            }
            if (mediaItem != null) {
                // Set title and artist
                binding.txtTitle.text = mediaItem.title
                binding.txtArtist.text = mediaItem.artist ?: "Unknown Artist"

                // Try to load album art from local path first
                var albumArtLoaded = false
                if (!mediaItem.thumbnailPath.isNullOrBlank()) {
                    Glide.with(this@AudioPlayerActivity)
                        .load(mediaItem.thumbnailPath)
                        .placeholder(R.drawable.ic_music)
                        .error(R.drawable.ic_music)
                        .into(binding.imgAlbumArt)
                    albumArtLoaded = true
                }
                // If no local album art, try to fetch embedded art from file
                if (!albumArtLoaded || mediaItem.thumbnailPath.isNullOrBlank()) {
                    val bitmap = AlbumArtFetcher.fetchEmbeddedAlbumArt(this@AudioPlayerActivity, mediaItem.uri)
                    if (bitmap != null) {
                        binding.imgAlbumArt.setImageDrawable(BitmapDrawable(resources, bitmap))
                    } else {
                        binding.imgAlbumArt.setImageResource(R.drawable.ic_music)
                    }
                }

                // Prepare media source using new API
                val dataSourceFactory = DefaultDataSource.Factory(this@AudioPlayerActivity)
                val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(mediaItem.uri)))
                player.setMediaSource(mediaSource)
                player.prepare()
                player.playWhenReady = playWhenReady
                player.seekTo(currentPosition)
                
                // Update lastPlayed timestamp
                withContext(Dispatchers.IO) {
                    mediaRepository.updateLastPlayed(mediaItemId!!)
                }
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
    
    private fun playNextTrack() {
        if (playlistId == -1L) {
            // No playlist, just finish the activity
            finish()
            return
        }
        
        lifecycleScope.launch {
            val playlistItems = withContext(Dispatchers.IO) {
                mediaRepository.getMediaItemsByPlaylistId(playlistId)
            }
            
            if (playlistItems.isNotEmpty()) {
                val currentIndex = playlistItems.indexOfFirst { it.id == mediaItemId }
                if (currentIndex >= 0 && currentIndex < playlistItems.size - 1) {
                    // Play next track
                    val nextItem = playlistItems[currentIndex + 1]
                    mediaItemId = nextItem.id
                    loadMediaItem()
                } else if (player.repeatMode == Player.REPEAT_MODE_ALL) {
                    // Play first track if repeating playlist
                    val firstItem = playlistItems[0]
                    mediaItemId = firstItem.id
                    loadMediaItem()
                } else {
                    // End of playlist
                    finish()
                }
            }
        }
    }
    
    private fun playPreviousTrack() {
        if (playlistId == -1L) {
            // No playlist, just restart current track
            player.seekTo(0)
            return
        }
        
        // If position is more than 3 seconds, restart current track
        if (player.currentPosition > 3000) {
            player.seekTo(0)
            return
        }
        
        lifecycleScope.launch {
            val playlistItems = withContext(Dispatchers.IO) {
                mediaRepository.getMediaItemsByPlaylistId(playlistId)
            }
            
            if (playlistItems.isNotEmpty()) {
                val currentIndex = playlistItems.indexOfFirst { it.id == mediaItemId }
                if (currentIndex > 0) {
                    // Play previous track
                    val previousItem = playlistItems[currentIndex - 1]
                    mediaItemId = previousItem.id
                    loadMediaItem()
                } else if (player.repeatMode == Player.REPEAT_MODE_ALL) {
                    // Play last track if repeating playlist
                    val lastItem = playlistItems.last()
                    mediaItemId = lastItem.id
                    loadMediaItem()
                } else {
                    // Restart current track
                    player.seekTo(0)
                }
            }
        }
    }
    
    private fun toggleShuffleMode() {
        val isShuffleOn = player.shuffleModeEnabled
        player.shuffleModeEnabled = !isShuffleOn
        
        // Update UI based on shuffle mode
        if (player.shuffleModeEnabled) {
            binding.btnShuffle.setColorFilter(getColor(R.color.accent))
        } else {
            binding.btnShuffle.clearColorFilter()
        }
    }
    
    private fun toggleRepeatMode() {
        val currentRepeatMode = player.repeatMode
        val newRepeatMode = when (currentRepeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_OFF
            else -> Player.REPEAT_MODE_OFF
        }
        player.repeatMode = newRepeatMode
        // Update UI based on repeat mode
        when (newRepeatMode) {
            Player.REPEAT_MODE_OFF -> {
                binding.btnRepeat.clearColorFilter()
                binding.btnRepeat.setImageResource(R.drawable.ic_repeat)
            }
            Player.REPEAT_MODE_ONE -> {
                binding.btnRepeat.setColorFilter(getColor(R.color.accent))
                binding.btnRepeat.setImageResource(R.drawable.ic_repeat_one)
            }
            Player.REPEAT_MODE_ALL -> {
                binding.btnRepeat.setColorFilter(getColor(R.color.accent))
                binding.btnRepeat.setImageResource(R.drawable.ic_repeat)
            }
        }
    }

    private fun fetchAndShowLyrics(artist: String?, title: String?) {
        if (artist.isNullOrBlank() || title.isNullOrBlank()) {
            Toast.makeText(this, "No lyrics found", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            val lyrics = withContext(Dispatchers.IO) {
                try {
                    val url = "https://api.lyrics.ovh/v1/${artist}/${title}"
                    val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000
                    val responseCode = connection.responseCode
                    if (responseCode == 200) {
                        val stream = connection.inputStream.bufferedReader().use { it.readText() }
                        val json = org.json.JSONObject(stream)
                        json.optString("lyrics", "")
                    } else {
                        ""
                    }
                } catch (e: Exception) {
                    ""
                }
            }
            if (!lyrics.isNullOrBlank()) {
                showLyricsDialog(lyrics)
            } else {
                Toast.makeText(this@AudioPlayerActivity, "No lyrics found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLyricsDialog(lyrics: String?) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Lyrics")
            .setMessage(lyrics ?: "No lyrics found")
            .setPositiveButton(android.R.string.ok, null)
            .create()
        dialog.show()
    }
}
