package com.andrei.entities.auth.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrei.entities.R
import com.andrei.entities.auth.data.AuthRepository
import com.andrei.entities.auth.data.TokenHolder
import com.andrei.entities.core.TAG
import kotlinx.coroutines.launch
import com.andrei.entities.core.Result

class LoginViewModel: ViewModel() {

    private val mutableLoginFormState = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = mutableLoginFormState

    private val mutableLoginResult = MutableLiveData<Result<TokenHolder>>()
    val loginResult: LiveData<Result<TokenHolder>> = mutableLoginResult

    fun login(username: String, password:String){

        viewModelScope.launch {
            Log.v(TAG, "login...")
            mutableLoginResult.value = AuthRepository.login(username, password)
        }
    }

    fun loginDataChanged(username: String, password: String){
        if (!isUserNameValid(username)){
            mutableLoginFormState.value = LoginFormState(usernameError = R.string.invalid_username)
        }
        else if (!isPasswordValid(password)) {
            mutableLoginFormState.value = LoginFormState(passwordError = R.string.invalid_password)
        }else{
            mutableLoginFormState.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isPasswordValid(password: String): Boolean {

        return password.length > 4
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')){
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else{
            username.isNotBlank()
        }
    }
}