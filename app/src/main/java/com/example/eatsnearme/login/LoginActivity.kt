package com.example.eatsnearme.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eatsnearme.R
import com.example.eatsnearme.MainActivity
import com.parse.LogInCallback
import com.parse.ParseException
import com.parse.ParseUser
import com.parse.SignUpCallback
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    companion object {
        const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.i(TAG, "in log in")
        uiComponents()

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity()
        }

    }
    private fun uiComponents() {
        btnLogin.setOnClickListener{
            Log.i(TAG, "onClick login button")
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            loginUser(username, password)
        }

        btnSignUp.setOnClickListener{
            Log.i(TAG, "onClick sign up button")
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            signUpUser(username, password)
        }
    }

    private fun loginUser(username: String, password: String) {
        Log.i(TAG, "attempting to log in user $username")
        ParseUser.logInInBackground(username, password, object : LogInCallback {
            override fun done(user: ParseUser?, e: ParseException?) {
                if (e != null) {
                    Log.e(TAG, "Issue with login", e)
                    Toast.makeText(
                        this@LoginActivity,
                        "Incorrect username or password",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                goMainActivity()
            }
        })
    }

    private fun signUpUser(username: String, password: String) {
        val user = ParseUser()
        user.username = username
        user.setPassword(password)
        user.signUpInBackground(object : SignUpCallback {
            override fun done(e: ParseException?) {
                if (e != null) {
                    Log.e(TAG, "Issue with sign up", e)
                    Toast.makeText(this@LoginActivity, "Username taken", Toast.LENGTH_LONG).show()
                    return
                }
                goMainActivity()
                Toast.makeText(this@LoginActivity, "Sign up success!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun goMainActivity() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        finish()
    }
}