package com.mediaplus.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "playlist_media_cross_ref",
    primaryKeys = ["playlistId", "mediaItemId"],
    foreignKeys = [
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MediaItem::class,
            parentColumns = ["id"],
            childColumns = ["mediaItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mediaItemId"])
    ]
)
data class PlaylistMediaCrossRef(
    val playlistId: Long,
    val mediaItemId: String,
    val position: Int
)
