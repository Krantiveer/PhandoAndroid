package com.perseverance.phando.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.perseverance.phando.db.dao.*


@Database(entities = arrayOf(
        Category::class,
        Filter::class,
        WatchLaterVideo::class,
        FavoriteVideo::class,
        Language::class,
        APIData::class
), version = 2)
@TypeConverters(RoomDataTypeConvertor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun filterDao(): FilterDao
    abstract fun watchLaterVideoDao(): WatchLaterVideoDao
    abstract fun favoriteVideoDao(): FavoriteVideoDao
    abstract fun languageDao(): LanguageDao
    abstract fun apiDataDao(): APIDataDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "Database"
        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build()
                }
            }
            return INSTANCE
        }

    }
}