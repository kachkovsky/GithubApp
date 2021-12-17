package com.github.kachkovsky.githubapp.ui.profiles

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kachkovsky.githubapp.R
import com.github.kachkovsky.githubapp.databinding.FragmentProfilesBinding
import com.github.kachkovsky.githubapp.ui.utils.EndlessScrollListener
import com.github.kachkovsky.githubapp.ui.utils.LayoutSwitch
import com.github.kachkovsky.infinitylistloader.ConcurrentRepository
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class ProfilesFragment : Fragment(), ConcurrentRepository.Updatable {

    companion object {
        const val MAX_GITHUB_LOGIN_LENGTH = 39
        val GITHUB_LOGIN_REGEX = "[a-zA-Z0-9\\-]*".toRegex()
    }

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
        adapter = ProfilesRecycleAdapter(profilesViewModel)
        binding.recyclerView.adapter = adapter
        prepareRecyclerView(activity)
        binding.noNetwork.buttonRetry.setOnClickListener { b ->
            layoutSwitch.showLayout(State.LOADING)
            profilesViewModel.listLoader.updateLoadedParts()
        }
        binding.addUser.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            builder.setTitle(getString(R.string.github_login_alert_title))
            val input = EditText(activity)

            input.setSingleLine()
            input.setFilters(
                arrayOf(
                    InputFilter.LengthFilter(MAX_GITHUB_LOGIN_LENGTH),
                    InputFilter { src, start, end, dst, dstart, dend ->
                        if (src == "") { // for backspace
                            return@InputFilter src
                        }
                        if (src.toString().matches(GITHUB_LOGIN_REGEX)) {
                            src
                        } else ""
                    })
            )
            input.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    input.post {
                        try {
                            val inputMethodManager =
                                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
                        } catch (e: Exception) {
                            Timber.d("Can't show keyboard")
                        }
                    }
                }
            }
            val container = FrameLayout(activity)
            val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_input_margin)
            params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_input_margin)
            input.layoutParams = params
            container.addView(input);
            //input.setInputType(InputType.TYPE_CLASS_TEXT)
            builder.setView(container)
            builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                val text = input.text.toString()
                if (text.length > 0) {
                    profilesViewModel.addLogin(text)
                }
            }
            builder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.cancel()
            }
            builder.show()
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