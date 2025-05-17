package com.mediaplus.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mediaplus.app.data.database.dao.MediaItemDao
import com.mediaplus.app.data.database.dao.PlaylistDao
import com.mediaplus.app.data.database.dao.PlaylistMediaCrossRefDao
import com.mediaplus.app.data.model.MediaItem
import com.mediaplus.app.data.model.Playlist
import com.mediaplus.app.data.model.PlaylistMediaCrossRef

@Database(
    entities = [
        MediaItem::class,
        Playlist::class,
        PlaylistMediaCrossRef::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MediaDatabase : RoomDatabase() {
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistMediaCrossRefDao(): PlaylistMediaCrossRefDao
}
