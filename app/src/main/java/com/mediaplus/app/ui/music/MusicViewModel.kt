package com.mediaplus.app.ui.music

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediaplus.app.data.model.MediaItem
import com.mediaplus.app.data.model.MediaType
import com.mediaplus.app.data.model.Playlist
import com.mediaplus.app.data.repository.MediaRepository
import com.mediaplus.app.data.repository.PlaylistRepository
import kotlinx.coroutines.launch

class MusicViewModel : ViewModel() {
    
    private val mediaRepository = MediaRepository()
    private val playlistRepository = PlaylistRepository()
    
    // LiveData for music
    val music: LiveData<List<MediaItem>> = mediaRepository.getMediaItemsByType(MediaType.AUDIO)
    
    // LiveData for playlists
    val playlists: LiveData<List<Playlist>> = playlistRepository.getAllPlaylists()
    
    // Add media item to playlist
    fun addToPlaylist(mediaItemId: String, playlistId: Long) {
        viewModelScope.launch {
            playlistRepository.addMediaItemToPlaylist(playlistId, mediaItemId)
        }
    }
    
    // Create new playlist and add media item to it
    fun createPlaylistAndAddMedia(name: String, description: String?, mediaItemId: String) {
        viewModelScope.launch {
            val playlistId = playlistRepository.createPlaylist(name, description)
            playlistRepository.addMediaItemToPlaylist(playlistId, mediaItemId)
        }
    }    fun sortMusic(option: String) {
        // Log the option selection (to use the parameter and remove warning)
        android.util.Log.d("MusicViewModel", "Sort option selected: $option")
        // Implementation will be added in future updates
        // For now the parameter is needed to match function signature in fragment
    }
}
