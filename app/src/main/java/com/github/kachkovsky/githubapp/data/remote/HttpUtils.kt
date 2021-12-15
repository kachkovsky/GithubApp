package com.github.kachkovsky.githubapp.data.remote

import okhttp3.CacheControl
import okhttp3.Interceptor

class HttpUtils {
    companion object {
        private val NETWORK_CACHE_CONTROL_INTERCEPTOR =
            Interceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .cacheControl(CacheControl.FORCE_NETWORK).build()
                )
            }
        private val LOCAL_CACHE_CONTROL_INTERCEPTOR =
            Interceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE).build()
                )
            }


    }
}