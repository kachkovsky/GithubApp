package com.github.kachkovsky.githubapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.kachkovsky.githubapp.data.entity.Profile
import com.github.kachkovsky.githubapp.data.entity.ProfileLogin

@Database(entities = [Profile::class, ProfileLogin::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao
    abstract fun profileLoginDao(): ProfileLoginDao

    companion object {
        private const val DB_NAME = "db_github"

        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) { instance ?: buildDb(context).also { instance = it } }

        private fun buildDb(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, "db_github")
                .fallbackToDestructiveMigration()
                .addCallback(rdc)
                .build()

        private val rdc = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL("Insert into profileLogin (login) values ('robhudson')")
                db.execSQL("Insert into profileLogin (login) values ('muffinresearch')")
                db.execSQL("Insert into profileLogin (login) values ('clarkbw')")
                db.execSQL("Insert into profileLogin (login) values ('claus')")
            }
        }
    }

}
