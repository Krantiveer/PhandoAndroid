package com.perseverance.phando.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.perseverance.phando.AdsUtil.AdModel
import com.perseverance.phando.AdsUtil.AdModelDao
import com.perseverance.phando.db.dao.*
import com.perseverance.phando.home.mediadetails.downloads.DownloadMetadata
import com.perseverance.phando.notification.NotificationDao
import com.perseverance.phando.notification.NotificationData
import com.perseverance.phando.payment.paymentoptions.WalletDetail


@Database(entities = arrayOf(
        Category::class,
        Filter::class,
        Language::class,
        APIData::class,
        DownloadMetadata::class,
        AdModel::class,
        WalletDetail::class,
        NotificationData::class
), version = 6)
@TypeConverters(RoomDataTypeConvertor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun filterDao(): FilterDao
    abstract fun languageDao(): LanguageDao
    abstract fun apiDataDao(): APIDataDao
    abstract fun downloadMetadataDao(): DownloadMetadataDao
    abstract fun adModelDao(): AdModelDao
    abstract fun notificationDao(): NotificationDao
    abstract fun walletDetailDao(): WalletDetailDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "Database"
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build()
                }
            }
            return INSTANCE!!
        }

    }
}