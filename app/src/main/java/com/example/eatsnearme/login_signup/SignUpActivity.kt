package com.example.eatsnearme.login_signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.eatsnearme.MainActivity
import com.example.eatsnearme.R
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.btnLogin
import kotlinx.android.synthetic.main.activity_signup.etPassword
import kotlinx.android.synthetic.main.activity_signup.etUsername

class SignUpActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SignUpActivity"
    }

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        Log.i(TAG, "in sign up")
        initializeButtons()
    }

    private fun initializeButtons() {
        btnCreateAccount.setOnClickListener {
            Log.i(TAG, "onClick sign up button")
            val email = etEmail.text.toString()
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            viewModel.signUpUser(email, username, password)
            signingUpUser(username)
        }

        btnLogin.setOnClickListener{
            Log.i(TAG, "onClick sign up button")
            goLoginActivity()
        }
    }

    private fun signingUpUser(username: String) {
        collectLatestLifecycleFlow(viewModel.stateFlow) {
            when (it) {
                is SignUpViewModel.SignUpState.Loading -> {
                    Log.i(TAG, "attempting to sign up $username")
                }
                is SignUpViewModel.SignUpState.Loaded -> {
                    if (it.message == "success") {
                        Toast.makeText(this, "Welcome $username!", Toast.LENGTH_SHORT).show()
                        goMainActivity()
                    }
                    else {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun goLoginActivity() {
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun goMainActivity() {
        val i = Intent(this@SignUpActivity, MainActivity::class.java)
        startActivity(i)
        finish()
    }


}