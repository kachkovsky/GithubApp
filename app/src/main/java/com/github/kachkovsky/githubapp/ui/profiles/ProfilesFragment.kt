package com.github.kachkovsky.githubapp.ui.profiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.kachkovsky.githubapp.ui.GithubProfileActivity
import com.github.kachkovsky.githubapp.databinding.FragmentProfilesBinding
import com.github.kachkovsky.githubapp.databinding.FragmentProjectsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilesFragment : Fragment() {

    private lateinit var profilesViewModel: ProfilesViewModel
    private var _binding: FragmentProfilesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profilesViewModel =
            ViewModelProvider(this).get(ProfilesViewModel::class.java)
        _binding = FragmentProfilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addUser.setOnClickListener {
            startActivity(GithubProfileActivity.intentFor(requireActivity(), "kachkovsky"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}