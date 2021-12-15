package com.github.kachkovsky.githubapp.data.remote

import com.github.kachkovsky.githubapp.data.Resource
import retrofit2.Response
import timber.log.Timber

abstract class BaseDataSource {

    protected suspend fun <T> convertResult(call: suspend () -> Response<T>): Resource<T> {
        val time = System.currentTimeMillis()
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) return Resource.success(body)
            }
            return error(time, " ${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(time, e.message ?: e.toString(), e)
        }
    }

    private fun <T> error(lastErrorTime: Long, message: String, e: Throwable? = null): Resource<T> {
        Timber.d(message)
        return Resource.error(message, e, lastErrorTime)
    }

}