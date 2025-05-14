package com.mediaplus.app.ui.videos

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mediaplus.app.data.model.MediaItem
import com.mediaplus.app.data.model.MediaType
import com.mediaplus.app.data.repository.MediaRepository

class VideosViewModel : ViewModel() {
      private val mediaRepository = MediaRepository()
    
    // LiveData for videos
    val videos: LiveData<List<MediaItem>> = mediaRepository.getMediaItemsByType(MediaType.VIDEO)
    
    fun sortVideos(option: String) {
        // Log the option selection (to use the parameter and remove warning)
        android.util.Log.d("VideosViewModel", "Sort option selected: $option")
        // Implementation will be added in future updates
    }
}
