package com.example.eatsnearme.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.eatsnearme.R
import com.example.eatsnearme.login_signup.LoginActivity
import com.example.eatsnearme.login_signup.SignUpViewModel
import com.example.eatsnearme.login_signup.collectLatestLifecycleFlow
import com.example.eatsnearme.restaurants.collectLatestLifecycleFlow
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeButtons()
    }

    private fun initializeButtons() {
        btnExitSettings.setOnClickListener{
            requireActivity().onBackPressed()
        }

        btnEditEmail.setOnClickListener{
            val email = etNewEmail.text.toString()
            viewModel.updateEmail(email)
            updatingProfile()
        }

        btnEditUsername.setOnClickListener{
            val username = etNewUsername.text.toString()
            viewModel.updateUsername(username)
            updatingProfile()
        }

        btnEditPassword.setOnClickListener{
            val oldPassword = etOldPassword.text.toString()
            val newPassword = etOldPassword.text.toString()
            viewModel.updatePassword(oldPassword, newPassword)
            updatingProfile()
        }
    }

    private fun updatingProfile() {
        collectLatestLifecycleFlow(viewModel.stateFlow) {
            when (it) {
                is SettingsViewModel.SettingsState.Loading -> {
                    Log.i(LoginActivity.TAG, "updating")
                }
                is SettingsViewModel.SettingsState.Loaded -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}