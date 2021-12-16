package com.github.kachkovsky.githubapp.ui.profiles

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kachkovsky.githubapp.ui.GithubProfileActivity
import com.github.kachkovsky.githubapp.databinding.FragmentProfilesBinding
import com.github.kachkovsky.githubapp.ui.projects.ProjectsRecycleAdapter
import com.github.kachkovsky.githubapp.ui.utils.EndlessScrollListener
import com.github.kachkovsky.githubapp.ui.utils.LayoutSwitch
import com.github.kachkovsky.infinitylistloader.ConcurrentRepository
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilesFragment : Fragment(), ConcurrentRepository.Updatable {

    private lateinit var profilesViewModel: ProfilesViewModel
    private var _binding: FragmentProfilesBinding? = null
    private val binding get() = _binding!!


    private lateinit var layoutSwitch: LayoutSwitch<State>

    private lateinit var adapter: ProfilesRecycleAdapter

    private fun createLayoutSwitch(): LayoutSwitch<State> {
        return LayoutSwitch(
            LayoutSwitch.LayoutObject(State.NO_NETWORK, binding.noNetwork.root),
            LayoutSwitch.LayoutObject(State.LOADING, binding.loading.root),
            LayoutSwitch.LayoutObject(State.CONTENT, binding.recyclerView),
            LayoutSwitch.LayoutObject(State.EMPTY, binding.emptyState)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilesBinding.inflate(inflater, container, false)
        profilesViewModel =
            ViewModelProvider(this).get(ProfilesViewModel::class.java)
        layoutSwitch = createLayoutSwitch()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //may be not need to bind, if binding in children
        val activity = requireActivity()
        adapter = ProfilesRecycleAdapter()
        binding.recyclerView.adapter = adapter
        prepareRecyclerView(activity)
        binding.noNetwork.buttonRetry.setOnClickListener { b ->
            layoutSwitch.showLayout(State.LOADING)
            profilesViewModel.listLoader.updateLoadedParts()
        }
        binding.addUser.setOnClickListener {
            startActivity(GithubProfileActivity.intentFor(requireActivity(), "kachkovsky"))
        }
    }

    override fun onResume() {
        super.onResume()
        profilesViewModel.listLoader.addUpdatable(this)
        update()
    }

    override fun onPause() {
        super.onPause()
        profilesViewModel.listLoader.removeUpdatable(this)
    }

    private fun prepareRecyclerView(context: Context?) {
        val lm = LinearLayoutManager(context)
        val listener: EndlessScrollListener = object : EndlessScrollListener(lm) {
            override fun onLoadMore(): Boolean {
                return profilesViewModel.listLoader.loadNextPart()
            }
        }
        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, lm.orientation))
        binding.recyclerView.layoutManager = lm
        binding.recyclerView.addOnScrollListener(listener)
    }

    private fun showSnack(message: String?) {
        Snackbar.make(binding.frameLayout, message!!, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    enum class State {
        CONTENT, LOADING, NO_NETWORK, EMPTY
    }

    override fun update() {
        val listResult = profilesViewModel.listLoader.result
        //TODO: need to disable refreshing only for network operations
        if (listResult.resultList == null) {
            if (listResult.errorMessage != null) {
                binding.noNetwork.noNetworkText.text = listResult.errorMessage
                layoutSwitch.showLayout(State.NO_NETWORK)
            } else if (listResult.isFinished) {
                layoutSwitch.showLayout(State.EMPTY)
            } else {
                layoutSwitch.showLayout(State.LOADING)
            }
        } else {
            if (listResult.errorMessage != null) {
                showSnack(listResult.errorMessage)
            }
            adapter.items = listResult.resultList
            layoutSwitch.showLayout(State.CONTENT)
        }
    }
}