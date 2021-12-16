package com.github.kachkovsky.githubapp.ui.profiles

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.kachkovsky.githubapp.data.entity.ProfileLogin
import com.github.kachkovsky.githubapp.databinding.ProfileLoginItemBinding
import com.github.kachkovsky.githubapp.ui.GithubProfileActivity
import com.github.kachkovsky.githubapp.ui.base.BaseListRecyclerAdapter

class ProfilesRecycleAdapter :
    BaseListRecyclerAdapter<List<ProfileLogin>, ProfilesRecycleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate: ProfileLoginItemBinding =
            ProfileLoginItemBinding.inflate(LayoutInflater.from(parent.context))
        val lp = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        inflate.layout.layoutParams = lp
        return ViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(items!![position])
        holder.binding.layout.setOnClickListener { view ->
            items?.get(position)?.let {
                view.context.startActivity(GithubProfileActivity.intentFor(view.context, it.login))
            }
        }
    }

    class ViewHolder(val binding: ProfileLoginItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setItem(data: ProfileLogin) {
            binding.twName.text = data.login
        }
    }
}