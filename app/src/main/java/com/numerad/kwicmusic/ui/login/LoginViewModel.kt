package com.numerad.kwicmusic.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.numerad.kwicmusic.R
import com.numerad.kwicmusic.data.LoginRepository
import com.numerad.kwicmusic.data.Result

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

        if (result is Result.Success) {
            _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }
}