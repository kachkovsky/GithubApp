package com.github.kachkovsky.githubapp.data.repository

import com.github.kachkovsky.githubapp.data.db.ProfileLoginDao
import com.github.kachkovsky.githubapp.data.entity.ProfileLogin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileLoginRepository @Inject constructor(
    private val profileLoginDao: ProfileLoginDao
) {

    fun addProfileLogin(scope: CoroutineScope, profileLogin: ProfileLogin, callback: () -> Unit) {
        scope.launch(Dispatchers.IO) {
            profileLoginDao.insert(profileLogin)
            scope.launch(Dispatchers.Main) {
                callback.invoke()
            }
        }
    }

    fun removeProfileLogin(scope: CoroutineScope, id: Long, callback: () -> Unit) {
        scope.launch(Dispatchers.IO) {
            profileLoginDao.delete(id)
            scope.launch(Dispatchers.Main) {
                callback.invoke()
            }
        }
    }

}