package com.github.kachkovsky.githubapp.data.remote

import com.github.kachkovsky.githubapp.data.entity.Profile
import com.github.kachkovsky.githubapp.data.entity.ProjectList
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {

    @GET("users/{login}")
    suspend fun getProfile(@Path("login") login: String): Response<Profile>

    @GET("/search/code?q=addClass+user:mozilla")
    fun getProjects(@Query("page") page: Int): Call<ProjectList>
}