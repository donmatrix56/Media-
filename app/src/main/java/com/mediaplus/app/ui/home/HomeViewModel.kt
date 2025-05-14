package com.mediaplus.app.ui.home

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediaplus.app.data.model.MediaItem
import com.mediaplus.app.data.model.MediaType
import com.mediaplus.app.data.repository.MediaRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    
    private val mediaRepository = MediaRepository()
    
    // LiveData for recent media
    val recentMedia: LiveData<List<MediaItem>> = mediaRepository.getAllMediaItems()
    
    // LiveData for videos
    val videos: LiveData<List<MediaItem>> = mediaRepository.getMediaItemsByType(MediaType.VIDEO)
    
    // LiveData for music
    val music: LiveData<List<MediaItem>> = mediaRepository.getMediaItemsByType(MediaType.AUDIO)
    
    private val _sortedRecent = MutableLiveData<List<MediaItem>>()
    val sortedRecent: LiveData<List<MediaItem>> = _sortedRecent
    private var lastRecentSort: String = "Date Created"

    private val _sortedVideos = MutableLiveData<List<MediaItem>>()
    val sortedVideos: LiveData<List<MediaItem>> = _sortedVideos
    private var lastVideosSort: String = "Date Created"

    private val _sortedMusic = MutableLiveData<List<MediaItem>>()
    val sortedMusic: LiveData<List<MediaItem>> = _sortedMusic
    private var lastMusicSort: String = "Date Created"

    init {
        recentMedia.observeForever { sortRecent(lastRecentSort) }
        videos.observeForever { sortVideos(lastVideosSort) }
        music.observeForever { sortMusic(lastMusicSort) }
    }

    fun addMediaFromUri(context: Context, uri: Uri, mediaType: MediaType) {
        viewModelScope.launch {
            // Use MediaRepository to scan and add the single file
            mediaRepository.addMediaFromUri(context, uri, mediaType)
        }
    }
    
    fun deleteAllDocumentProviderMedia() {
        viewModelScope.launch {
            mediaRepository.deleteAllDocumentProviderMedia()
        }
    }
    
    fun deleteMediaItem(mediaItem: MediaItem) {
        viewModelScope.launch {
            mediaRepository.deleteMediaItem(mediaItem)
        }
    }
      // Remove item from recent list only
    fun removeFromRecent(mediaItem: MediaItem) {
        mediaRepository.removeFromRecent(mediaItem.id)
    }
    
    fun scanMediaFiles(context: Context) {
        viewModelScope.launch {
            mediaRepository.scanMediaFiles(context)
        }
    }
    
    fun sortRecent(option: String) {
        lastRecentSort = option
        val items = recentMedia.value ?: return
        _sortedRecent.value = sortMediaList(items, option)
    }

    fun sortVideos(option: String) {
        lastVideosSort = option
        val items = videos.value ?: return
        _sortedVideos.value = sortMediaList(items, option)
    }

    fun sortMusic(option: String) {
        lastMusicSort = option
        val items = music.value ?: return
        _sortedMusic.value = sortMediaList(items, option)
    }    private fun sortMediaList(list: List<MediaItem>, option: String): List<MediaItem> {
        return when (option) {
            "Date Created" -> list.sortedByDescending { it.dateAdded }
            "A-Z" -> list.sortedBy { it.title.lowercase() }
            "Size (Big to Small)" -> list.sortedByDescending { it.size }
            else -> list
        }
    }
}
