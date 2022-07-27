package com.example.eatsnearme.login_signup

import androidx.lifecycle.ViewModel
import com.parse.LogInCallback
import com.parse.ParseException
import com.parse.ParseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    private val _stateFlow = MutableStateFlow<LoginState>(LoginState.Loading)
    val stateFlow: StateFlow<LoginState> = _stateFlow

    fun loginUser(username: String, password: String) {
        _stateFlow.value = LoginState.Loading
        ParseUser.logInInBackground(username, password, object : LogInCallback {
            override fun done(user: ParseUser?, e: ParseException?) {
                if (e != null) {
                    _stateFlow.tryEmit(LoginState.Loaded(false))
                    return
                }
                _stateFlow.tryEmit(LoginState.Loaded(true))
            }
        })
    }

    sealed class LoginState {
        object Loading : LoginState()
        data class Loaded(var success : Boolean): LoginState()
    }

}