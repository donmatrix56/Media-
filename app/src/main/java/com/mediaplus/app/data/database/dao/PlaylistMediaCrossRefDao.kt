package com.mediaplus.app.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mediaplus.app.data.model.MediaItem
import com.mediaplus.app.data.model.PlaylistMediaCrossRef

@Dao
interface PlaylistMediaCrossRefDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistMediaCrossRef(crossRef: PlaylistMediaCrossRef)
    
    @Delete
    suspend fun deletePlaylistMediaCrossRef(crossRef: PlaylistMediaCrossRef)
    
    @Query("DELETE FROM playlist_media_cross_ref WHERE playlistId = :playlistId AND mediaItemId = :mediaItemId")
    suspend fun deletePlaylistMediaCrossRef(playlistId: Long, mediaItemId: String)
    
    @Query("DELETE FROM playlist_media_cross_ref WHERE playlistId = :playlistId")
    suspend fun deleteAllMediaItemsFromPlaylist(playlistId: Long)
    
    @Query("SELECT * FROM media_items INNER JOIN playlist_media_cross_ref ON media_items.id = playlist_media_cross_ref.mediaItemId WHERE playlist_media_cross_ref.playlistId = :playlistId ORDER BY playlist_media_cross_ref.position")
    @RewriteQueriesToDropUnusedColumns
    fun getMediaItemsForPlaylist(playlistId: Long): LiveData<List<MediaItem>>
    
    @Query("SELECT COUNT(*) FROM playlist_media_cross_ref WHERE playlistId = :playlistId")
    suspend fun getPlaylistMediaCount(playlistId: Long): Int
    
    @Query("UPDATE playlist_media_cross_ref SET position = :newPosition WHERE playlistId = :playlistId AND mediaItemId = :mediaItemId")
    suspend fun updatePlaylistMediaPosition(playlistId: Long, mediaItemId: String, newPosition: Int)
}
