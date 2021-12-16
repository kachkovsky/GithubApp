package com.github.kachkovsky.githubapp.ui.projects

import androidx.lifecycle.ViewModel
import com.github.kachkovsky.githubapp.data.loader.ProjectsLoaderFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    factory: ProjectsLoaderFactory
) : ViewModel() {

    val infinityListLoader = factory.getLoader()

    override fun onCleared() {
        infinityListLoader.dispose()
    }

}