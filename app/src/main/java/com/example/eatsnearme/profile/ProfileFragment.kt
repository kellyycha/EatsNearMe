package com.example.eatsnearme.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eatsnearme.R
import com.example.eatsnearme.login_signup.LoginActivity
import com.parse.ParseUser
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

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
            ParseUser.logOutInBackground()
            goLoginActivity()
        }
    }

    private fun goLoginActivity() {
        val i = Intent(requireContext(), LoginActivity::class.java)
        startActivity(i)
    }

}