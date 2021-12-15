package com.github.kachkovsky.githubapp.data

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val errorMessage: String?,
    val throwable: Throwable?,
    val lastErrorTime: Long?
) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(
            data: T?,
            message: String? = null,
            throwable: Throwable? = null,
            lastErrorTime: Long? = null
        ): Resource<T> {
            return Resource(Status.SUCCESS, data, message, throwable, lastErrorTime)
        }

        fun <T> error(
            message: String,
            throwable: Throwable? = null,
            lastErrorTime: Long,
            data: T? = null
        ): Resource<T> {
            return Resource(Status.ERROR, data, message, throwable, lastErrorTime)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null, null, null)
        }
    }
}