package com.github.kachkovsky.githubapp.data.remote

import android.content.Context
import com.google.gson.Gson
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File

class HttpUtils {
    companion object {
        private const val GITHUB_API_URL = "https://api.github.com"
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

        fun createDefaultRetrofit(gson: Gson): Retrofit {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging).build()
            return Retrofit.Builder()
                .baseUrl(GITHUB_API_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

        fun createCache(context: Context): Cache {
            val httpCacheDirectory = File(context.cacheDir, "responses")
            val cacheSize = 10 * 1024 * 1024 // 10 MiB
            return Cache(httpCacheDirectory, cacheSize.toLong())
        }


        fun createCacheableRetrofit(
            context: Context,
            gson: Gson,
            cache: Cache,
            network: Boolean
        ): Retrofit {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val builder = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(if (network) NETWORK_CACHE_CONTROL_INTERCEPTOR else LOCAL_CACHE_CONTROL_INTERCEPTOR)
            if (network) {
                builder.addNetworkInterceptor(Interceptor { chain ->
                    val response = chain.proceed(chain.request())
                    response.newBuilder()
                        .header("Cache-Control", "max-age=2147483647")
                        .build()
                })
            }
            val okHttpClient: OkHttpClient = builder.cache(cache)
                .build()
            return Retrofit.Builder()
                .baseUrl(GITHUB_API_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
    }

    class RetrofitHolder(context: Context, gson: Gson, cache: Cache) {
        val cacheRetrofit = createCacheableRetrofit(context, gson, cache, false)
        val forceRetrofit = createCacheableRetrofit(context, gson, cache, true)
    }
}