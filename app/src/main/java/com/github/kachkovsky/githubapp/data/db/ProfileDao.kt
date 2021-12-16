package com.github.kachkovsky.githubapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.kachkovsky.githubapp.data.entity.Profile

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profile WHERE login = :login COLLATE NOCASE")
    fun get(login: String): LiveData<Profile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: Profile)

}