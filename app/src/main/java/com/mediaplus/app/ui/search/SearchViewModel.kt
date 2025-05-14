package com.mediaplus.app.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediaplus.app.data.model.MediaItem
import com.mediaplus.app.data.repository.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {
    
    private val mediaRepository = MediaRepository()
    
    // LiveData for search results
    private val _searchResults = MutableLiveData<List<MediaItem>>()
    val searchResults: LiveData<List<MediaItem>> = _searchResults
    
    // Search media items
    fun searchMedia(query: String) {
        viewModelScope.launch {
            val results = withContext(Dispatchers.IO) {
                mediaRepository.searchMediaItems(query)
            }
            _searchResults.postValue(results)
        }
    }
    
    fun deleteMediaItem(mediaItem: MediaItem) {
        viewModelScope.launch {
            mediaRepository.deleteMediaItem(mediaItem)
            // Remove from current search results
            _searchResults.value = _searchResults.value?.filter { it.id != mediaItem.id }
        }
    }
}
