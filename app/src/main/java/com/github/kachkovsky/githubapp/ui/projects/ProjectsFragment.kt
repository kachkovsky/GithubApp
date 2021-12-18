package com.github.kachkovsky.githubapp.ui.projects

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

    private val projectsViewModel: ProjectsViewModel by viewModels()

    private var _binding: FragmentProjectsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProjectsRecycleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectsBinding.inflate(inflater, container, false)
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
            showLoading()
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

    override fun update() {
        val listResult = projectsViewModel.listLoader.result
        //TODO: need to disable refreshing only for network operations
        binding.swipeRefreshLayout.isRefreshing = false
        if (listResult.resultList == null) {
            if (listResult.errorMessage != null) {
                binding.noNetwork.noNetworkText.text = listResult.errorMessage
                showNoNetwork()
            } else if (listResult.isFinished) {
                showEmpty()
            } else {
                showLoading()
            }
        } else {
            if (listResult.errorMessage != null) {
                showSnack(listResult.errorMessage)
            }
            adapter.items = listResult.resultList
            showContent()
        }
    }

    private fun showLoading() {
        binding.loading.root.visibility = View.VISIBLE
        binding.noNetwork.root.visibility = View.GONE
        binding.swipeRefreshLayout.visibility = View.GONE
    }

    private fun showEmpty() {
        binding.loading.root.visibility = View.GONE
        binding.noNetwork.root.visibility = View.GONE
        binding.swipeRefreshLayout.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
    }

    private fun showContent() {
        binding.loading.root.visibility = View.GONE
        binding.noNetwork.root.visibility = View.GONE
        binding.swipeRefreshLayout.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE
    }

    private fun showNoNetwork() {
        binding.loading.root.visibility = View.GONE
        binding.noNetwork.root.visibility = View.VISIBLE
        binding.swipeRefreshLayout.visibility = View.GONE
    }
}