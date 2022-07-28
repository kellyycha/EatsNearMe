package com.example.eatsnearme.login_signup


import androidx.lifecycle.ViewModel
import com.parse.ParseException
import com.parse.ParseUser
import com.parse.SignUpCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SignUpViewModel : ViewModel() {

    private val _stateFlow = MutableStateFlow<SignUpState>(SignUpState.Loading)
    val stateFlow: StateFlow<SignUpState> = _stateFlow

    fun signUpUser(email: String, username: String, password: String) {
        _stateFlow.value = SignUpState.Loading

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            _stateFlow.tryEmit(SignUpState.Loaded("Please fill out all fields"))
        }

        val user = ParseUser()
        user.email = email
        user.username = username
        user.setPassword(password)

        user.signUpInBackground(object : SignUpCallback {
            override fun done(e: ParseException?) {
                e?.let{ error ->
                    error.message?.let{
                        _stateFlow.tryEmit(SignUpState.Loaded(it))
                        _stateFlow.value = SignUpState.Loading
                        return
                    }
                }
                _stateFlow.tryEmit(SignUpState.Loaded("success"))
            }
        })
    }

    sealed class SignUpState {
        object Loading : SignUpState()
        data class Loaded(var message : String): SignUpState()
    }
}