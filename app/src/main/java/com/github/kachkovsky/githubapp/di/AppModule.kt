package com.github.kachkovsky.githubapp.di

import android.content.Context
import com.github.kachkovsky.githubapp.data.db.AppDatabase
import com.github.kachkovsky.githubapp.data.db.ProfileDao
import com.github.kachkovsky.githubapp.data.loader.ProfileLoaderFactory
import com.github.kachkovsky.githubapp.data.loader.ProjectsLoaderFactory
import com.github.kachkovsky.githubapp.data.remote.GithubRemoteDataSource
import com.github.kachkovsky.githubapp.data.remote.GithubService
import com.github.kachkovsky.githubapp.data.remote.HttpUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit = HttpUtils.createDefaultRetrofit(gson)

    @Singleton
    @Provides
    fun provideRetrofitHolder(
        @ApplicationContext appContext: Context,
        gson: Gson,
        cache: Cache
    ): HttpUtils.RetrofitHolder = HttpUtils.RetrofitHolder(appContext, gson, cache)

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Singleton
    @Provides
    fun provideCache(@ApplicationContext appContext: Context): Cache =
        HttpUtils.createCache(appContext)

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


    @Provides
    fun provideProjectsLoaderFactory(
        @ApplicationContext appContext: Context,
        retrofitHolder: HttpUtils.RetrofitHolder
    ) =
        ProjectsLoaderFactory(appContext, retrofitHolder)
}