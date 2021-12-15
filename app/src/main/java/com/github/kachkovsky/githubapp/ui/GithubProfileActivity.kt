package com.github.kachkovsky.githubapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.kachkovsky.githubapp.R
import com.github.kachkovsky.githubapp.ui.profiledetails.ProfileDetailsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GithubProfileActivity : AppCompatActivity() {

    companion object {


        fun intentFor(ctx: Context, login: String): Intent {
            return Intent(ctx, GithubProfileActivity::class.java).apply {
                putExtra(ProfileDetailsFragment.LOGIN_EXTRA, login)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.github_profile_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    ProfileDetailsFragment.newInstance(
                        intent.getStringExtra(ProfileDetailsFragment.LOGIN_EXTRA) ?: ""
                    )
                )
                .commitNow()
        }
    }


}