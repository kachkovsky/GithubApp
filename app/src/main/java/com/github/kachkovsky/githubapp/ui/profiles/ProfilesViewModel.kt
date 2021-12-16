package com.github.kachkovsky.githubapp.ui.profiles

import androidx.lifecycle.ViewModel
import com.github.kachkovsky.githubapp.data.loader.ProfileLoginLoaderFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfilesViewModel @Inject constructor(
    factory: ProfileLoginLoaderFactory
) : ViewModel() {
    val listLoader = factory.getLoader()

    init {
        listLoader.updateLoadedParts()
    }
}