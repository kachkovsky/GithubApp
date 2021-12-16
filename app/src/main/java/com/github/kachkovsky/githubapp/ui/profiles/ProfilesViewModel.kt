package com.github.kachkovsky.githubapp.ui.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kachkovsky.githubapp.data.entity.ProfileLogin
import com.github.kachkovsky.githubapp.data.loader.ProfileLoginLoaderFactory
import com.github.kachkovsky.githubapp.data.repository.ProfileLoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfilesViewModel @Inject constructor(
    factory: ProfileLoginLoaderFactory,
    val profileLoginRepository: ProfileLoginRepository,
) : ViewModel() {

    val listLoader = factory.getLoader()

    init {
        listLoader.updateLoadedParts()
    }

    fun addLogin(text: String) {
        profileLoginRepository.addProfileLogin(viewModelScope, ProfileLogin(0, text)) {
            listLoader.updateLoadedParts()
        }
    }

    fun removeProfileLogin(id: Long) {
        profileLoginRepository.removeProfileLogin(viewModelScope, id) {
            listLoader.updateLoadedParts()
        }
    }
}