package com.github.kachkovsky.githubapp.data.loader

import android.content.Context
import com.github.kachkovsky.githubapp.data.entity.Project
import com.github.kachkovsky.githubapp.data.entity.ProjectList
import com.github.kachkovsky.githubapp.data.remote.GithubService
import com.github.kachkovsky.githubapp.data.remote.HttpUtils
import com.github.kachkovsky.infinitylistloader.InfinityListLoader
import com.github.kachkovsky.infinitylistloader.RequestResult
import com.github.kachkovsky.infinitylistloader.SourceLoader
import com.github.kachkovsky.infinitylistloader.combiningrepomnses.DefaultResponseCombiner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class ProjectsLoaderFactory @Inject constructor(
    val appContext: Context,
    retrofitHolder: HttpUtils.RetrofitHolder
) {
    private val githubNetworkService =
        retrofitHolder.forceRetrofit.create(GithubService::class.java)
    private val githubCachedService = retrofitHolder.cacheRetrofit.create(GithubService::class.java)

    fun getLoader(): InfinityListLoader<Project, String> =
        InfinityListLoader.createConcurrentCacheLoader(
            DefaultResponseCombiner(appContext),
            { partIndex: Int, callback: SourceLoader.RRConsumer<RequestResult<Project, String>> ->
                githubNetworkService.getProjects(partIndex + 1)
                    .enqueue(object : Callback<ProjectList> {
                        override fun onResponse(
                            call: Call<ProjectList>,
                            response: Response<ProjectList>
                        ) {
                            val empty =
                                response.body() == null || response.body()?.items.isNullOrEmpty()
                            var items: List<Project>? = null
                            var message: String? = null
                            if (!empty) {
                                items = response.body()?.items
                            } else if (!response.message().isNullOrEmpty()) {
                                message = " ${response.code()} ${response.message()}"
                            }
                            val result =
                                RequestResult<Project, String>(items, !empty, empty, message)
                            callback.accept(result)
                        }

                        override fun onFailure(call: Call<ProjectList>, t: Throwable) {
                            Timber.d(t)
                            val result = RequestResult<Project, String>(
                                null,
                                false,
                                !(t is UnknownHostException || t is SocketTimeoutException),
                                t.toString()
                            )
                            callback.accept(result)
                        }
                    })

            },
            { partIndex: Int, callback: SourceLoader.RRConsumer<RequestResult<Project, String>> ->
                githubCachedService.getProjects(partIndex + 1)
                    .enqueue(object : Callback<ProjectList> {
                        override fun onResponse(
                            call: Call<ProjectList>,
                            response: Response<ProjectList>
                        ) {
                            val empty =
                                response.body() == null || response.body()?.items.isNullOrEmpty()
                            var items: List<Project>? = null
                            var message: String? = null
                            if (!empty) {
                                items = response.body()?.items
                            } else if (!response.message().isNullOrEmpty()) {
                                message = " ${response.code()} ${response.message()}"
                            }
                            val result =
                                RequestResult<Project, String>(items, !empty, empty, message)
                            callback.accept(result)
                        }

                        override fun onFailure(call: Call<ProjectList>, t: Throwable) {
                            Timber.d(t)
                            val result = RequestResult<Project, String>(
                                null,
                                false,
                                true,
                                t.toString()
                            )
                            callback.accept(result)
                        }
                    })
            })
}