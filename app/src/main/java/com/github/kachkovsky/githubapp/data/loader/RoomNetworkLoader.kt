package com.github.kachkovsky.githubapp.data.loader

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.github.kachkovsky.githubapp.data.CombinedLiveData
import com.github.kachkovsky.githubapp.data.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomNetworkLoader<T, R>(
    private val localCall: () -> LiveData<T>,
    private val networkCall: suspend () -> Resource<R>,
    private val saveResultLocally: suspend (R) -> Unit
) {
    private val remoteLiveData = MutableLiveData<Resource<R>>()
    val resultLiveData: CombinedLiveData<Resource<R>, Resource<T>, Resource<T>> =
        CombinedLiveData(
            remoteLiveData,
            localCall.invoke().map {
                if (it != null) {
                    Resource.success(it)
                } else {
                    Resource.loading(it)
                }
            }
        ) { remote, local ->
            var r: Resource<T>? = null
            if (local != null) {
                r = local
            }
            if (r == null) {
                r = Resource.loading()
            }
            if (remote?.errorMessage != null) {
                if (Resource.Status.LOADING == r.status) {
                    r = Resource.error(
                        remote.errorMessage,
                        remote.throwable,
                        remote.lastErrorTime!!
                    )
                } else {
                    r = Resource.success(
                        r.data,
                        remote.errorMessage,
                        remote.throwable,
                        remote.lastErrorTime!!
                    )
                }
            }
            r
        }

    fun loadFromNetwork(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            val response = networkCall.invoke()
            if (response.status == Resource.Status.SUCCESS) {
                saveResultLocally(response.data!!)
            }
            remoteLiveData.postValue(response)
        }
    }

}