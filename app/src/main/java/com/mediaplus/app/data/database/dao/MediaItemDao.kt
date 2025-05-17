package com.mediaplus.app.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mediaplus.app.data.model.MediaItem
import com.mediaplus.app.data.model.MediaType

@Dao
interface MediaItemDao {
      @Query("SELECT * FROM media_items")
    fun getAllMediaItems(): LiveData<List<MediaItem>>
    
    @Query("SELECT * FROM media_items")
    suspend fun getAllMediaItemsSync(): List<MediaItem>
    
    @Query("SELECT * FROM media_items WHERE mediaType = :mediaType")
    fun getMediaItemsByType(mediaType: MediaType): LiveData<List<MediaItem>>
    
    @Query("SELECT * FROM media_items WHERE isFavorite = 1")
    fun getFavoriteMediaItems(): LiveData<List<MediaItem>>
    
    @Query("SELECT * FROM media_items WHERE id = :mediaItemId")
    suspend fun getMediaItemById(mediaItemId: String): MediaItem?
    
    @Query("SELECT * FROM media_items WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' OR album LIKE '%' || :query || '%'")
    suspend fun searchMediaItems(query: String): List<MediaItem>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItems(mediaItems: List<MediaItem>)
    
    @Update
    suspend fun updateMediaItem(mediaItem: MediaItem)
      @Query("SELECT media_items.* FROM media_items INNER JOIN playlist_media_cross_ref ON media_items.id = playlist_media_cross_ref.mediaItemId WHERE playlist_media_cross_ref.playlistId = :playlistId ORDER BY playlist_media_cross_ref.position")
    suspend fun getMediaItemsForPlaylistSync(playlistId: Long): List<MediaItem>
    
    @Query("SELECT * FROM media_items WHERE lastPlayed > 0 ORDER BY lastPlayed DESC")
    fun getRecentlyPlayedItems(): LiveData<List<MediaItem>>
    
    @Delete
    suspend fun deleteMediaItem(mediaItem: MediaItem)
      @Query("DELETE FROM media_items WHERE id = :mediaItemId")
    suspend fun deleteMediaItemById(mediaItemId: String)
    
    @Query("DELETE FROM media_items WHERE uri LIKE 'content://com.android.externalstorage.documents/%' OR uri LIKE 'content://com.android.providers.media.documents/%'")
    suspend fun deleteAllDocumentProviderMedia()
}
