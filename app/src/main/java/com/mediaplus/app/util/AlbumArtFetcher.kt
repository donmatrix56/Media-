package com.mediaplus.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AlbumArtFetcher {
    /**
     * Attempts to extract embedded album art from a local audio file URI.
     * Returns a Bitmap if found, or null if not present.
     */
    suspend fun fetchEmbeddedAlbumArt(context: Context, audioUri: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, android.net.Uri.parse(audioUri))
                val art = retriever.embeddedPicture
                retriever.release()
                if (art != null) BitmapFactory.decodeByteArray(art, 0, art.size) else null
            } catch (e: Exception) {
                null
            }
        }
    }
}
