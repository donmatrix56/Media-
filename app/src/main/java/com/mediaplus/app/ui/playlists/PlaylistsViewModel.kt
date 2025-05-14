package com.mediaplus.app.ui.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediaplus.app.data.model.Playlist
import com.mediaplus.app.data.repository.PlaylistRepository
import kotlinx.coroutines.launch

class PlaylistsViewModel : ViewModel() {
    
    private val playlistRepository = PlaylistRepository()
    
    // LiveData for playlists
    val playlists: LiveData<List<Playlist>> = playlistRepository.getAllPlaylists()
    
    // Create a new playlist
    fun createPlaylist(name: String, description: String?) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name, description)
        }
    }
    
    // Delete a playlist by ID
    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
        }
    }
}
