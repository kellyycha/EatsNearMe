package com.example.eatsnearme.login_signup

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.parse.ParseException
import com.parse.ParseUser
import com.parse.SignUpCallback

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    // copy and paste from the login activity before
//    fun signUpUser(username: String, password: String) {
//        val user = ParseUser()
//        user.username = username
//        user.setPassword(password)
//        user.signUpInBackground(object : SignUpCallback {
//            override fun done(e: ParseException?) {
//                if (e != null) {
//                    Log.e(LoginActivity.TAG, "Issue with sign up", e)
//                    Toast.makeText(getApplication(), "Username taken", Toast.LENGTH_LONG).show()
//                    return
//                }
//                LoginActivity().goMainActivity()
//                Toast.makeText(getApplication(), "Sign up success!", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
}