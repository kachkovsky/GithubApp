package com.github.kachkovsky.githubapp.data.loader

import android.content.Context
import com.github.kachkovsky.githubapp.data.db.ProfileLoginDao
import com.github.kachkovsky.githubapp.data.entity.ProfileLogin
import com.github.kachkovsky.infinitylistloader.InfinityListLoader
import com.github.kachkovsky.infinitylistloader.RequestResult
import com.github.kachkovsky.infinitylistloader.SourceLoader
import com.github.kachkovsky.infinitylistloader.combiningrepomnses.DefaultResponseCombiner
import javax.inject.Inject

class ProfileLoginLoaderFactory @Inject constructor(
    val appContext: Context,
    val profileLoginDao: ProfileLoginDao,
) {

    fun getLoader(): InfinityListLoader<ProfileLogin, String> =
        InfinityListLoader.createNetworkOnlyLoader(
            DefaultResponseCombiner(appContext)
        ) { partIndex: Int, callback: SourceLoader.RRConsumer<RequestResult<ProfileLogin, String>> ->
            val profileLoginList =
                profileLoginDao.getProfileLoginWithOffset(LIMIT, partIndex * LIMIT)
            callback.accept(
                RequestResult<ProfileLogin, String>(
                    profileLoginList,
                    true,
                    LIMIT != profileLoginList.size.toLong(),
                    null
                )
            )
        }

    companion object {
        const val LIMIT = 20L
    }
}