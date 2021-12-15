package com.github.kachkovsky.githubapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class Profile(
    @PrimaryKey
    val id: Long,
    val login: String,
    val name: String?,
    val avatar_url: String?,
    val bio: String?,
    val company: String?
)