package com.mediaplus.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaItem(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String? = null,
    val album: String? = null,
    val duration: Long = 0,
    val path: String,
    val uri: String,
    val mediaType: MediaType,
    val mimeType: String? = null,
    val thumbnailPath: String? = null,
    val dateAdded: Long = 0,
    val size: Long = 0,
    val isFavorite: Boolean = false,
    val lastPlayed: Long = 0
)

enum class MediaType {
    AUDIO,
    VIDEO,
    IMAGE
}
