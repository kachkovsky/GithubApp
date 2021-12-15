package com.github.kachkovsky.githubapp.data.loader

import com.github.kachkovsky.githubapp.data.db.ProfileDao
import com.github.kachkovsky.githubapp.data.remote.GithubRemoteDataSource
import javax.inject.Inject

class ProfileLoaderFactory @Inject constructor(
    private val remoteDataSource: GithubRemoteDataSource,
    private val localDataSource: ProfileDao
) {

    fun getLoader(login: String) = RoomNetworkLoader(
        localCall = { localDataSource.get(login) },
        networkCall = { remoteDataSource.getProfile(login) },
        saveResultLocally = { localDataSource.insert(it) }
    )
}