package com.github.kachkovsky.githubapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.kachkovsky.githubapp.data.entity.Profile

@Database(entities = [Profile::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao

    companion object {
        private const val DB_NAME = "db_github"
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) { instance ?: buildDb(context).also { instance = it } }

        private fun buildDb(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, "db_github")
                .fallbackToDestructiveMigration()
                .build()
    }

}