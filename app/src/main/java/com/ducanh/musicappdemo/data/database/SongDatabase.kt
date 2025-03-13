package com.ducanh.musicappdemo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ducanh.musicappdemo.data.dao.SongDao
import com.ducanh.musicappdemo.data.entity.Song

@Database(entities = [Song::class], version = 1, exportSchema = false)
abstract class SongDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao

    companion object {
        const val DATABASE_NAME = "dictionary.db"

//        private var INSTANCE: SongDatabase? = null
//
//        fun getDatabase(context: Context): SongDatabase? {
//            if (INSTANCE == null) {
//                synchronized(SongDatabase::class) {
//                    INSTANCE = Room.databaseBuilder(
//                        context.applicationContext,
//                        SongDatabase::class.java, "room.db"
//                    ).allowMainThreadQueries()
//                        .fallbackToDestructiveMigration()
//                        .build()
//                }
//            }
//            return INSTANCE
//        }
//
//        fun destroyInstance() {
//            INSTANCE = null
//        }
    }
}