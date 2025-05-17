package com.mediaplus.app.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import com.mediaplus.app.MediaPlusApp
import com.mediaplus.app.data.database.dao.MediaItemDao
import com.mediaplus.app.data.model.MediaItem
import com.mediaplus.app.data.model.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaRepository {
    private val mediaItemDao: MediaItemDao = MediaPlusApp.database.mediaItemDao()
      // Get all media items from database
    fun getAllMediaItems(): LiveData<List<MediaItem>> {
        return mediaItemDao.getAllMediaItems()
    }
    
    // Get recently played items (items with lastPlayed > 0)
    fun getRecentlyPlayedItems(): LiveData<List<MediaItem>> {
        return mediaItemDao.getRecentlyPlayedItems()
    }
    
    // Get media items by type
    fun getMediaItemsByType(mediaType: MediaType): LiveData<List<MediaItem>> {
        return mediaItemDao.getMediaItemsByType(mediaType)
    }
    
    // Get favorite media items
    fun getFavoriteMediaItems(): LiveData<List<MediaItem>> {
        return mediaItemDao.getFavoriteMediaItems()
    }
    
    // Get a specific media item by ID
    suspend fun getMediaItemById(mediaItemId: String): MediaItem? {
        return mediaItemDao.getMediaItemById(mediaItemId)
    }
    
    // Search for media items
    suspend fun searchMediaItems(query: String): List<MediaItem> {
        return mediaItemDao.searchMediaItems(query)
    }
      // Update a media item
    suspend fun updateMediaItem(mediaItem: MediaItem) {
        mediaItemDao.updateMediaItem(mediaItem)
    }
    
    // Update lastPlayed timestamp for a media item
    suspend fun updateLastPlayed(mediaItemId: String) {
        val mediaItem = mediaItemDao.getMediaItemById(mediaItemId)
        mediaItem?.let {
            val updatedItem = it.copy(lastPlayed = System.currentTimeMillis())
            mediaItemDao.updateMediaItem(updatedItem)
        }
    }
    
    // Toggle favorite status of a media item
    suspend fun toggleFavorite(mediaItemId: String) {
        val mediaItem = mediaItemDao.getMediaItemById(mediaItemId)
        mediaItem?.let {
            val updatedItem = it.copy(isFavorite = !it.isFavorite)
            mediaItemDao.updateMediaItem(updatedItem)
        }
    }
    
    // Delete all media items with DocumentProvider URIs (old entries without persisted permissions)
    suspend fun deleteAllDocumentProviderMedia() {
        withContext(Dispatchers.IO) {
            mediaItemDao.deleteAllDocumentProviderMedia()
        }
    }
    
    // Delete a specific media item
    suspend fun deleteMediaItem(mediaItem: MediaItem) {
        withContext(Dispatchers.IO) {
            mediaItemDao.deleteMediaItem(mediaItem)
        }
    }
      // Scan device for media files
    suspend fun scanMediaFiles(context: Context) {
        withContext(Dispatchers.IO) {
            val audioItems = scanAudioFiles(context)
            val videoItems = scanVideoFiles(context)
            mediaItemDao.insertMediaItems(audioItems + videoItems)
        }
    }    // Scan device for videos only (optimized for quicker scanning)
    // Preserves recent files by not affecting lastPlayed timestamps
    suspend fun scanVideoFilesOnly(context: Context) {
        withContext(Dispatchers.IO) {
            // Get existing media items first to preserve lastPlayed values
            val existingItems = mediaItemDao.getAllMediaItemsSync()
            val existingMap = existingItems.associateBy { it.uri }
            
            // Scan for videos
            val videoItems = scanVideoFiles(context)
            
            // Process scanned items to preserve lastPlayed timestamps
            val processedItems = videoItems.map { newItem ->
                // If the item already exists, keep its lastPlayed value
                existingMap[newItem.uri]?.let { existingItem ->
                    newItem.copy(lastPlayed = existingItem.lastPlayed)
                } ?: newItem
            }
            
            // Insert merged items
            mediaItemDao.insertMediaItems(processedItems)
        }
    }
    
    // Scan device for audio only (optimized for quicker scanning)
    // Preserves recent files by not affecting lastPlayed timestamps
    suspend fun scanAudioFilesOnly(context: Context) {
        withContext(Dispatchers.IO) {
            // Get existing media items first to preserve lastPlayed values
            val existingItems = mediaItemDao.getAllMediaItemsSync()
            val existingMap = existingItems.associateBy { it.uri }
            
            // Scan for audio files
            val audioItems = scanAudioFiles(context)
            
            // Process scanned items to preserve lastPlayed timestamps
            val processedItems = audioItems.map { newItem ->
                // If the item already exists, keep its lastPlayed value
                existingMap[newItem.uri]?.let { existingItem ->
                    newItem.copy(lastPlayed = existingItem.lastPlayed)
                } ?: newItem
            }
            
            // Insert merged items
            mediaItemDao.insertMediaItems(processedItems)
        }
    }
    
    // Scan for audio files on the device
    private suspend fun scanAudioFiles(context: Context): List<MediaItem> {
        return withContext(Dispatchers.IO) {
            val audioItems = mutableListOf<MediaItem>()
            
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM_ID
            )
            
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
            
            context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn).toString()
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        cursor.getLong(idColumn)
                    )
                    
                    val albumId = cursor.getLong(albumIdColumn)
                    val albumArtUri = Uri.parse("content://media/external/audio/albumart/$albumId")
                    
                    val mediaItem = MediaItem(
                        id = id,
                        title = cursor.getString(titleColumn),
                        artist = cursor.getString(artistColumn),
                        album = cursor.getString(albumColumn),
                        duration = cursor.getLong(durationColumn),
                        path = cursor.getString(pathColumn),
                        uri = contentUri.toString(),
                        mediaType = MediaType.AUDIO,
                        mimeType = cursor.getString(mimeTypeColumn),
                        thumbnailPath = albumArtUri.toString(),
                        dateAdded = cursor.getLong(dateAddedColumn),
                        size = cursor.getLong(sizeColumn)
                    )
                    audioItems.add(mediaItem)
                }
            }
            
            audioItems
        }
    }
    
    // Scan for video files on the device
    private suspend fun scanVideoFiles(context: Context): List<MediaItem> {
        return withContext(Dispatchers.IO) {
            val videoItems = mutableListOf<MediaItem>()
            
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
            
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.SIZE
            )
            
            val sortOrder = "${MediaStore.Video.Media.TITLE} ASC"
            
            context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn).toString()
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        cursor.getLong(idColumn)
                    )
                    
                    val mediaItem = MediaItem(
                        id = id,
                        title = cursor.getString(titleColumn),
                        duration = cursor.getLong(durationColumn),
                        path = cursor.getString(pathColumn),
                        uri = contentUri.toString(),
                        mediaType = MediaType.VIDEO,
                        mimeType = cursor.getString(mimeTypeColumn),
                        thumbnailPath = null,  // Will be loaded separately
                        dateAdded = cursor.getLong(dateAddedColumn),
                        size = cursor.getLong(sizeColumn)                    )
                    videoItems.add(mediaItem)
                }
            }
            
            videoItems
        }
    }
    
    // Get media items for a playlist
    suspend fun getMediaItemsByPlaylistId(playlistId: Long): List<MediaItem> {
        return mediaItemDao.getMediaItemsForPlaylistSync(playlistId)
    }

    // Add media from URI
    suspend fun addMediaFromUri(context: Context, uri: Uri, mediaType: MediaType) {
        withContext(Dispatchers.IO) {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val id = System.currentTimeMillis().toString() + uri.toString().hashCode()
                    val title = it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                    val path = uri.toString()
                    // Safely get duration if present, else use 0L
                    val durationCol = it.getColumnIndex(MediaStore.MediaColumns.DURATION)
                    val duration = if ((mediaType == MediaType.AUDIO || mediaType == MediaType.VIDEO) && durationCol != -1) {
                        it.getLong(durationCol)
                    } else 0L
                    val size = it.getLong(it.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
                    val mimeType = it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
                    val mediaItem = com.mediaplus.app.data.model.MediaItem(
                        id = id,
                        title = title,
                        artist = null,
                        album = null,
                        duration = duration,
                        path = path,
                        uri = path,
                        mediaType = mediaType,
                        mimeType = mimeType,
                        thumbnailPath = null,
                        dateAdded = System.currentTimeMillis(),
                        size = size
                    )
                    mediaItemDao.insertMediaItems(listOf(mediaItem))
                }
            }
        }
    }    // Remove item from recent list only
    suspend fun removeFromRecent(mediaItemId: String) {
        withContext(Dispatchers.IO) {
            try {
                val mediaItem = mediaItemDao.getMediaItemById(mediaItemId)
                mediaItem?.let {
                    // Update the item to set lastPlayed to 0 to remove it from recent list
                    val updatedItem = it.copy(lastPlayed = 0L)
                    mediaItemDao.updateMediaItem(updatedItem)
                    Log.d("MediaRepository", "Removed item $mediaItemId from recent list")
                }
            } catch (e: Exception) {
                Log.e("MediaRepository", "Error removing item from recent list", e)
            }
        }
    }
}
