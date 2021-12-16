package com.github.kachkovsky.githubapp.ui.profiledetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kachkovsky.githubapp.data.entity.Profile
import com.github.kachkovsky.githubapp.data.loader.ProfileLoaderFactory
import com.github.kachkovsky.githubapp.data.loader.RoomNetworkLoader
import com.github.kachkovsky.githubapp.ui.utils.ShowErrorHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileDetailsViewModel @Inject constructor(
    val loaderFactory: ProfileLoaderFactory
) : ViewModel() {

    lateinit var loader: RoomNetworkLoader<Profile, Profile>
    val showErrorHelper = ShowErrorHelper()
    private var login: String? = null

    fun setLogin(login: String) {
        if (this.login != login) {
            loader = loaderFactory.getLoader(login)
            loader.loadFromNetwork(viewModelScope)
            this.login = login
        }
    }
}