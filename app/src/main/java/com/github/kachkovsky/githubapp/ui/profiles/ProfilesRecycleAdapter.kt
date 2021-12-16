package com.github.kachkovsky.githubapp.ui.profiles

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.github.kachkovsky.githubapp.R
import com.github.kachkovsky.githubapp.data.entity.ProfileLogin
import com.github.kachkovsky.githubapp.databinding.ProfileLoginItemBinding
import com.github.kachkovsky.githubapp.ui.GithubProfileActivity
import com.github.kachkovsky.githubapp.ui.base.BaseListRecyclerAdapter

class ProfilesRecycleAdapter(val viewModel: ProfilesViewModel) :
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
        val layout = holder.binding.layout
        layout.setOnClickListener { view ->
            items?.get(position)?.let {
                view.context.startActivity(GithubProfileActivity.intentFor(view.context, it.login))
            }
        }
        layout.setOnLongClickListener { view ->
            val pm = PopupMenu(layout.context, layout)
            pm.inflate(R.menu.profile_login_popup)
            pm.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_remove -> {
                        items?.get(position)?.let {
                            viewModel.removeProfileLogin(it.id)
                        }
                        true
                    }
                    else -> false
                }
            }
            pm.show()
            true
        }
    }

    class ViewHolder(val binding: ProfileLoginItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setItem(data: ProfileLogin) {
            binding.twName.text = data.login
        }
    }
}