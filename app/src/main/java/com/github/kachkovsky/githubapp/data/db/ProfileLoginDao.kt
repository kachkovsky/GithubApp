package com.github.kachkovsky.githubapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.kachkovsky.githubapp.data.entity.ProfileLogin

@Dao
interface ProfileLoginDao {

    @Query("SELECT * FROM profileLogin LIMIT :limit  OFFSET :offset")
    fun getProfileLoginWithOffset(limit: Long, offset: Long): List<ProfileLogin>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profileLogin: ProfileLogin)

    @Query("DELETE FROM profileLogin WHERE id = :id")
    suspend fun delete(id: Long)

}