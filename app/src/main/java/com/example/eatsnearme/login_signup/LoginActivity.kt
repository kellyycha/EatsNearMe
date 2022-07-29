package com.example.eatsnearme.login_signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.eatsnearme.R
import com.example.eatsnearme.MainActivity
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "LoginActivity"
    }

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeButtons()

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity()
        }


    }
    private fun initializeButtons() {
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            if (username.isEmpty() || password.isEmpty()){
                Toast.makeText(this,
                    "Please fill out all fields",
                    Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.loginUser(username, password)
            loggingInUser(username)
        }

        btnSignUp.setOnClickListener{
            goSignUpActivity()
        }
    }

    private fun loggingInUser(username: String) {
        collectLatestLifecycleFlow(viewModel.stateFlow) {
            when (it) {
                is LoginViewModel.LoginState.Loading -> {
                    Log.i(TAG, "attempting to log in $username")
                }
                is LoginViewModel.LoginState.Loaded -> {
                    if (!it.success) {
                        Toast.makeText(this,
                            "Invalid username or password",
                            Toast.LENGTH_LONG).show()
                    }
                    if (it.success) {
                        goMainActivity()
                    }

                }
            }
        }
    }

    private fun goSignUpActivity() {
        val i = Intent(this, SignUpActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun goMainActivity() {
        val i = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(i)
        finish()
    }
}

fun <T> ComponentActivity.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED){
            flow.collectLatest(collect)
        }
    }
}
