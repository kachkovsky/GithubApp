package com.github.kachkovsky.githubapp.ui.profiles

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kachkovsky.githubapp.databinding.FragmentProfilesBinding
import com.github.kachkovsky.githubapp.ui.utils.EndlessScrollListener
import com.github.kachkovsky.githubapp.ui.utils.LayoutSwitch
import com.github.kachkovsky.infinitylistloader.ConcurrentRepository
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfilesFragment : Fragment(), ConcurrentRepository.Updatable {

    companion object {
        const val ADD_PROFILE_LOGIN_DIALOG_FRAGMENT_TAG = "ADD_PROFILE_LOGIN_DIALOG_FRAGMENT_TAG"
    }

    private val profilesViewModel: ProfilesViewModel by viewModels()
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

        layoutSwitch = createLayoutSwitch()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //may be not need to bind, if binding in children
        val activity = requireActivity()
        adapter = ProfilesRecycleAdapter(profilesViewModel)
        binding.recyclerView.adapter = adapter
        prepareRecyclerView(activity)
        binding.noNetwork.buttonRetry.setOnClickListener { b ->
            layoutSwitch.showLayout(State.LOADING)
            profilesViewModel.listLoader.updateLoadedParts()
        }
        binding.addUser.setOnClickListener {
            //profilesViewModel.addLogin(text)
            val fragment = AddProfileLoginDialogFragment()
            fragment.show(childFragmentManager, ADD_PROFILE_LOGIN_DIALOG_FRAGMENT_TAG)
        }
        childFragmentManager.setFragmentResultListener(
            AddProfileLoginDialogFragment.DIALOG_RESULT,
            this
        ) { key, bundle ->
            val result = bundle.getString(AddProfileLoginDialogFragment.PROFILE_LOGIN_KEY)
            if (result != null) {
                profilesViewModel.addLogin(result)
            }
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