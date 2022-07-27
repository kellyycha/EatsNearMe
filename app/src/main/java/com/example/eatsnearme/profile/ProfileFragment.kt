package com.example.eatsnearme.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.eatsnearme.MainActivity
import com.example.eatsnearme.R
import com.example.eatsnearme.login_signup.LoginActivity
import com.parse.ParseUser
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvUsername.text = ParseUser.getCurrentUser().username.toString()

        initializeButtons()
    }

    private fun initializeButtons() {
        btnLogout.setOnClickListener{
            Log.i(MainActivity.TAG, "attempting to log out")
            ParseUser.logOutInBackground()
            goLoginActivity()
        }

        btnUsername.setOnClickListener{
            viewModel.changeUsername()
        }

        btnPassword.setOnClickListener{
            viewModel.changePassword()
        }

        btnEmail.setOnClickListener{
            viewModel.changeEmail()
        }
    }

    private fun goLoginActivity() {
        val i = Intent(requireContext(), LoginActivity::class.java)
        startActivity(i)
    }

}