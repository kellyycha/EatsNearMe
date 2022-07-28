package com.example.eatsnearme.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.eatsnearme.login_signup.LoginViewModel
import com.example.eatsnearme.login_signup.SignUpViewModel
import com.parse.LogInCallback
import com.parse.ParseException
import com.parse.ParseUser
import com.parse.SignUpCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class SettingsViewModel : ViewModel() {

    private val _stateFlow = MutableStateFlow<SettingsState>(SettingsState.Loading)
    val stateFlow: StateFlow<SettingsState> = _stateFlow

    fun updateEmail(email: String) {
        _stateFlow.value = SettingsState.Loading

        // check if email is email and already taken
        ParseUser.getCurrentUser().email = email

        _stateFlow.tryEmit(SettingsState.Loaded("Email Updated"))
    }

    fun updateUsername(username: String) {
        _stateFlow.value = SettingsState.Loading

        val user = ParseUser()
        user.username = username
//        user.setPassword("k")
        user.signUpInBackground(object : SignUpCallback {
            override fun done(e: ParseException?) {
                e?.let{ error ->
                    error.message?.let{
                        _stateFlow.tryEmit(SettingsState.Loaded("Username is taken"))
                        return
                    }
                }
                ParseUser.getCurrentUser().username = username
                _stateFlow.tryEmit(SettingsState.Loaded("Username Updated"))
            }
        })
    }

    fun updatePassword(oldPassword: String, newPassword: String) {
        _stateFlow.value = SettingsState.Loading

        if (newPassword.isEmpty()){
            _stateFlow.tryEmit(SettingsState.Loaded("Password cannot be blank"))
        }

        ParseUser.logInInBackground(ParseUser.getCurrentUser().username, oldPassword) { user, e ->
            if (user != null) {
                ParseUser.getCurrentUser().setPassword(newPassword)
                ParseUser.getCurrentUser().saveInBackground()
                _stateFlow.tryEmit(SettingsState.Loaded("Password Updated"))
            }
            _stateFlow.tryEmit(SettingsState.Loaded("Current password is incorrect"))
        }
    }

    sealed class SettingsState {
        object Loading : SettingsState()
        data class Loaded(var message : String): SettingsState()
    }
}