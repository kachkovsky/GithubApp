package com.github.kachkovsky.githubapp.data.remote

import javax.inject.Inject

class GithubRemoteDataSource @Inject constructor(
    private val githubService: GithubService
): BaseDataSource() {

    suspend fun getProfile(login: String) = convertResult { githubService.getProfile(login) }
}