package com.mediaplus.app.ui.playlists

import androidx.lifecycle.*
import com.mediaplus.app.data.model.MediaItem
import com.mediaplus.app.data.model.Playlist
import com.mediaplus.app.data.repository.PlaylistRepository
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(private val playlistId: Long) : ViewModel() {
    
    private val playlistRepository = PlaylistRepository()
    
    // LiveData for the playlist
    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist
    
    // LiveData for media items in the playlist
    val playlistItems: LiveData<List<MediaItem>> = playlistRepository.getMediaItemsForPlaylist(playlistId)
    
    init {
        // Load the playlist details when initialized
        loadPlaylist()
    }
    
    private fun loadPlaylist() {
        viewModelScope.launch {
            val loadedPlaylist = playlistRepository.getPlaylistById(playlistId)
            _playlist.postValue(loadedPlaylist)
        }
    }
    
    // Method to remove media item from playlist
    fun removeFromPlaylist(mediaItemId: String) {
        viewModelScope.launch {
            playlistRepository.removeMediaItemFromPlaylist(playlistId, mediaItemId)
        }
    }
    
    // Method to clear all items from playlist
    fun clearPlaylist() {
        viewModelScope.launch {
            playlistRepository.clearPlaylist(playlistId)
        }
    }
    
    // Method to reorder media item within the playlist
    fun moveMediaItem(mediaItemId: String, newPosition: Int) {
        viewModelScope.launch {
            playlistRepository.moveMediaItemInPlaylist(playlistId, mediaItemId, newPosition)
        }
    }
}

// ViewModel Factory to pass the playlist ID
class PlaylistDetailViewModelFactory(private val playlistId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaylistDetailViewModel(playlistId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
