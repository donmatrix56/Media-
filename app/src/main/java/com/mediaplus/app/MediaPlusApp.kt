package com.mediaplus.app

import android.app.Application
import androidx.room.Room
import com.mediaplus.app.data.database.MediaDatabase

class MediaPlusApp : Application() {

    companion object {
        lateinit var instance: MediaPlusApp
            private set
        
        lateinit var database: MediaDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize database
        database = Room.databaseBuilder(
            applicationContext,
            MediaDatabase::class.java,
            "media_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
