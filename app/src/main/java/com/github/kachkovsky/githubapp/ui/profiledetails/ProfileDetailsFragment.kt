package com.github.kachkovsky.githubapp.ui.profiledetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.github.kachkovsky.githubapp.R
import com.github.kachkovsky.githubapp.data.Resource
import com.github.kachkovsky.githubapp.data.entity.Profile
import com.github.kachkovsky.githubapp.databinding.ProfileDetailsFragmentBinding
import com.github.kachkovsky.githubapp.ui.utils.LayoutSwitch
import com.github.kachkovsky.githubapp.ui.utils.LayoutSwitch.LayoutObject
import com.github.kachkovsky.githubapp.ui.utils.ShowErrorHelper
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetailsFragment : Fragment() {

    companion object {
        const val LOGIN_EXTRA = "login"
        fun newInstance(login: String): ProfileDetailsFragment {
            val fragment = ProfileDetailsFragment()
            fragment.arguments = bundleOf("login" to login)
            return fragment
        }
    }

    private lateinit var layoutSwitch: LayoutSwitch<Resource.Status>
    private var _binding: ProfileDetailsFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val detailsViewModel: ProfileDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileDetailsFragmentBinding.inflate(inflater, container, false)
        layoutSwitch = LayoutSwitch(
            LayoutObject(Resource.Status.ERROR, binding.scrollView),
            LayoutObject(Resource.Status.LOADING, binding.loading.root),
            LayoutObject(Resource.Status.SUCCESS, binding.scrollView)
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(LOGIN_EXTRA)?.let { detailsViewModel.setLogin(it) }


        detailsViewModel.loader.resultLiveData.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    it.data?.let {
                        bindProfile(it)
                    }
                    if (it.errorMessage != null && detailsViewModel.showErrorHelper.needShowError(it.lastErrorTime!!)) {
                        Snackbar.make(binding.frameLayout, it.errorMessage, Snackbar.LENGTH_LONG)
                            .show()
                    }
                    layoutSwitch.showLayout(Resource.Status.SUCCESS)
                }
                Resource.Status.ERROR -> {
                    if (it.errorMessage != null && detailsViewModel.showErrorHelper.needShowError(it.lastErrorTime!!)) {
                        Snackbar.make(binding.frameLayout, it.errorMessage, Snackbar.LENGTH_LONG)
                            .show()
                    }
                    layoutSwitch.showLayout(Resource.Status.ERROR)
                }
                Resource.Status.LOADING -> {
                    layoutSwitch.showLayout(Resource.Status.LOADING)
                }
            }
        })
    }

    private fun bindProfile(profile: Profile) {
        val sb = StringBuilder().append(getString(R.string.login)).append(" : ")
            .append(profile.login)
        profile.name?.let {
            sb.append("\n").append(getString(R.string.name)).append(" : ").append(profile.name)
        }
        profile.bio?.let {
            sb.append("\n").append(getString(R.string.bio)).append(" : ").append(profile.bio)
        }
        profile.company?.let {
            sb.append("\n").append(getString(R.string.company)).append(" : ")
                .append(profile.company)
        }
        binding.textView.text = sb.toString()
        Glide.with(this).load(profile.avatar_url)
            .into(binding.image)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}