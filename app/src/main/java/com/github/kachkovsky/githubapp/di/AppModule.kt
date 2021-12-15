package com.github.kachkovsky.githubapp.di

import android.content.Context
import com.github.kachkovsky.githubapp.data.db.AppDatabase
import com.github.kachkovsky.githubapp.data.db.ProfileDao
import com.github.kachkovsky.githubapp.data.loader.ProfileLoaderFactory
import com.github.kachkovsky.githubapp.data.remote.GithubRemoteDataSource
import com.github.kachkovsky.githubapp.data.remote.GithubService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideCache(appContext: Context): Cache {
        val httpCacheDirectory: File = File(appContext.getCacheDir(), "responses")
        val cacheSize = 10L * 1024L * 1024L // 10 MiB
        return Cache(httpCacheDirectory, cacheSize)
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideGithubService(retrofit: Retrofit): GithubService =
        retrofit.create(GithubService::class.java)

    @Singleton
    @Provides
    fun provideGithubRemoteDataSource(characterService: GithubService) =
        GithubRemoteDataSource(characterService)

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context) =
        AppDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideProfileDao(db: AppDatabase) = db.profileDao()

    @Singleton
    @Provides
    fun provideProfileLoaderFactory(
        remoteDataSource: GithubRemoteDataSource,
        localDataSource: ProfileDao
    ) =
        ProfileLoaderFactory(remoteDataSource, localDataSource)
}