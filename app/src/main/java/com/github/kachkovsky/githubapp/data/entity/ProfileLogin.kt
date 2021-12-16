package com.github.kachkovsky.githubapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profileLogin")
data class ProfileLogin(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val login: String,
)