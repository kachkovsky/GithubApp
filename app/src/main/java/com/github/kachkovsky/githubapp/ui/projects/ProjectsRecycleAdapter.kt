package com.github.kachkovsky.githubapp.ui.projects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.kachkovsky.githubapp.data.entity.Project
import com.github.kachkovsky.githubapp.databinding.ProjectItemBinding
import com.github.kachkovsky.githubapp.ui.base.BaseListRecyclerAdapter

class ProjectsRecycleAdapter :
    BaseListRecyclerAdapter<List<Project>, ProjectsRecycleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate: ProjectItemBinding =
            ProjectItemBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(items!!.get(position))
    }

    class ViewHolder(val binding: ProjectItemBinding) : RecyclerView.ViewHolder(binding.getRoot()) {

        fun setItem(data: Project) {
            binding.twName.text = data.name
            binding.twDescription.text = data.repository?.description ?: ""
        }
    }
}