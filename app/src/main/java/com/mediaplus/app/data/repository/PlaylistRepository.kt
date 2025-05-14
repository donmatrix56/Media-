package com.mediaplus.app.data.repository

import androidx.lifecycle.LiveData
import com.mediaplus.app.MediaPlusApp
import com.mediaplus.app.data.database.dao.PlaylistDao
import com.mediaplus.app.data.database.dao.PlaylistMediaCrossRefDao
import com.mediaplus.app.data.model.MediaItem
import com.mediaplus.app.data.model.Playlist
import com.mediaplus.app.data.model.PlaylistMediaCrossRef

class PlaylistRepository {
    private val playlistDao: PlaylistDao = MediaPlusApp.database.playlistDao()
    private val playlistMediaCrossRefDao: PlaylistMediaCrossRefDao = MediaPlusApp.database.playlistMediaCrossRefDao()
    
    // Get all playlists
    fun getAllPlaylists(): LiveData<List<Playlist>> {
        return playlistDao.getAllPlaylists()
    }
    
    // Get a specific playlist by ID
    suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return playlistDao.getPlaylistById(playlistId)
    }
    
    // Create a new playlist
    suspend fun createPlaylist(name: String, description: String? = null): Long {
        val playlist = Playlist(
            name = name,
            description = description
        )
        return playlistDao.insertPlaylist(playlist)
    }
    
    // Update a playlist
    suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlist)
    }
    
    // Delete a playlist
    suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylistById(playlistId)
    }
    
    // Get all media items in a playlist
    fun getMediaItemsForPlaylist(playlistId: Long): LiveData<List<MediaItem>> {
        return playlistMediaCrossRefDao.getMediaItemsForPlaylist(playlistId)
    }
    
    // Add a media item to a playlist
    suspend fun addMediaItemToPlaylist(playlistId: Long, mediaItemId: String) {
        val count = playlistMediaCrossRefDao.getPlaylistMediaCount(playlistId)
        val crossRef = PlaylistMediaCrossRef(
            playlistId = playlistId,
            mediaItemId = mediaItemId,
            position = count
        )
        playlistMediaCrossRefDao.insertPlaylistMediaCrossRef(crossRef)
        
        // Update the playlist's updatedAt timestamp
        playlistDao.getPlaylistById(playlistId)?.let { playlist ->
            playlistDao.updatePlaylist(playlist.copy(updatedAt = System.currentTimeMillis()))
        }
    }
    
    // Remove a media item from a playlist
    suspend fun removeMediaItemFromPlaylist(playlistId: Long, mediaItemId: String) {
        playlistMediaCrossRefDao.deletePlaylistMediaCrossRef(playlistId, mediaItemId)
        
        // Update the playlist's updatedAt timestamp
        playlistDao.getPlaylistById(playlistId)?.let { playlist ->
            playlistDao.updatePlaylist(playlist.copy(updatedAt = System.currentTimeMillis()))
        }
    }
    
    // Reorder a media item within a playlist
    suspend fun moveMediaItemInPlaylist(playlistId: Long, mediaItemId: String, newPosition: Int) {
        playlistMediaCrossRefDao.updatePlaylistMediaPosition(playlistId, mediaItemId, newPosition)
        
        // Update the playlist's updatedAt timestamp
        playlistDao.getPlaylistById(playlistId)?.let { playlist ->
            playlistDao.updatePlaylist(playlist.copy(updatedAt = System.currentTimeMillis()))
        }
    }
    
    // Clear all media items from a playlist
    suspend fun clearPlaylist(playlistId: Long) {
        playlistMediaCrossRefDao.deleteAllMediaItemsFromPlaylist(playlistId)
        
        // Update the playlist's updatedAt timestamp
        playlistDao.getPlaylistById(playlistId)?.let { playlist ->
            playlistDao.updatePlaylist(playlist.copy(updatedAt = System.currentTimeMillis()))
        }
    }
}
