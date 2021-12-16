package com.github.kachkovsky.githubapp.ui.projects

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kachkovsky.githubapp.databinding.FragmentProjectsBinding
import com.github.kachkovsky.githubapp.ui.utils.EndlessScrollListener
import com.github.kachkovsky.githubapp.ui.utils.LayoutSwitch
import com.github.kachkovsky.infinitylistloader.ConcurrentRepository
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectsFragment : Fragment(), ConcurrentRepository.Updatable {

    private lateinit var projectsViewModel: ProjectsViewModel
    private lateinit var layoutSwitch: LayoutSwitch<State>

    private var _binding: FragmentProjectsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProjectsRecycleAdapter

    private fun createLayoutSwitch(): LayoutSwitch<State> {
        return LayoutSwitch(
            LayoutSwitch.LayoutObject(State.NO_NETWORK, binding.noNetwork.root),
            LayoutSwitch.LayoutObject(State.LOADING, binding.loading.root),
            LayoutSwitch.LayoutObject(State.CONTENT, binding.swipeRefreshLayout),
            LayoutSwitch.LayoutObject(State.EMPTY, binding.emptyState)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectsBinding.inflate(inflater, container, false)
        projectsViewModel = ViewModelProvider(this).get(ProjectsViewModel::class.java)
        layoutSwitch = createLayoutSwitch()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //may be not need to bind, if binding in children
        val activity = requireActivity()
        adapter = ProjectsRecycleAdapter()
        binding.recyclerView.adapter = adapter
        prepareRecyclerView(activity)
        projectsViewModel.listLoader.updateLoadedParts()
        binding.swipeRefreshLayout.setOnRefreshListener {
            projectsViewModel.listLoader.updateLoadedParts()
        }
        binding.noNetwork.buttonRetry.setOnClickListener { b ->
            layoutSwitch.showLayout(State.LOADING)
            projectsViewModel.listLoader.updateLoadedParts()
        }
    }

    override fun onResume() {
        super.onResume()
        projectsViewModel.listLoader.addUpdatable(this)
        update()
    }

    override fun onPause() {
        super.onPause()
        projectsViewModel.listLoader.removeUpdatable(this)
    }

    private fun prepareRecyclerView(context: Context?) {
        val lm = LinearLayoutManager(context)
        val listener: EndlessScrollListener = object : EndlessScrollListener(lm) {
            override fun onLoadMore(): Boolean {
                return projectsViewModel.listLoader.loadNextPart()
            }
        }
        binding.recyclerView.addItemDecoration(DividerItemDecoration(context, lm.orientation))
        binding.recyclerView.layoutManager = lm
        binding.recyclerView.addOnScrollListener(listener)
    }

    private fun showSnack(message: String?) {
        Snackbar.make(binding.swipeRefreshLayout, message!!, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    enum class State {
        CONTENT, LOADING, NO_NETWORK, EMPTY
    }

    override fun update() {
        val listResult = projectsViewModel.listLoader.result
        //TODO: need to disable refreshing only for network operations
        binding.swipeRefreshLayout.isRefreshing = false
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