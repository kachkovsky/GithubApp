package com.github.kachkovsky.githubapp.data.remote

import com.github.kachkovsky.githubapp.data.entity.Profile
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubService {

        @GET("users/{login}")
        suspend fun getProfile(@Path("login") login: String): Response<Profile>
}