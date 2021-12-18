package com.github.kachkovsky.githubapp.ui.profiles

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.github.kachkovsky.githubapp.R
import com.github.kachkovsky.githubapp.databinding.ProfileLoginDialogBinding
import timber.log.Timber

class AddProfileLoginDialogFragment : DialogFragment() {
    companion object {
        const val MAX_GITHUB_LOGIN_LENGTH = 39
        val GITHUB_LOGIN_REGEX = "[a-zA-Z0-9\\-]*".toRegex()

        const val DIALOG_RESULT = "AddProfileLoginDialogFragmentResult"
        const val PROFILE_LOGIN_KEY = "PROFILE_LOGIN_EXTRA"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.github_login_alert_title))
        val binding = ProfileLoginDialogBinding.inflate(LayoutInflater.from(context))
        val input = binding.editTextLogin
        input.filters = arrayOf(
            InputFilter.LengthFilter(MAX_GITHUB_LOGIN_LENGTH),
            InputFilter { src, _, _, _, _, _ ->
                if (src == "") { // for backspace
                    return@InputFilter src
                }
                if (src.toString().matches(GITHUB_LOGIN_REGEX)) {
                    src
                } else ""
            })
        input.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.post {
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
        builder.setView(binding.root)
        builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
            val text = input.text.toString()
            if (text.isNotEmpty()) {
                parentFragmentManager.setFragmentResult(
                    DIALOG_RESULT,
                    bundleOf(PROFILE_LOGIN_KEY to text)
                )
            }
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }
        return builder.create()
    }

    interface AddProfileLoginDialogListener {
        fun onAddProfileLoginDialogAccepted()
    }
}