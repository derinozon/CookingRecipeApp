package com.agileavengers.cooking_recipes_android.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.agileavengers.cooking_recipes_android.data.LoginRepository
import com.agileavengers.cooking_recipes_android.data.Result

import com.agileavengers.cooking_recipes_android.R
import com.agileavengers.cooking_recipes_android.ui.login.LoggedInUserView
import com.agileavengers.cooking_recipes_android.ui.login.LoginFormState
import com.agileavengers.cooking_recipes_android.ui.login.LoginResult
import kotlinx.coroutines.launch

class RegisterViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun register(email: String, username: String, password: String) {
        viewModelScope.launch {
            // can be launched in a separate asynchronous job
            val result = loginRepository.register(email, username, password)

            if (result is Result.Success) {
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(result.data.username, result.data.email))
            } else {
                _loginResult.value = LoginResult(error = R.string.register_failed)
            }
        }

    }

    fun loginDataChanged(email: String, username: String, password: String, passwordConfirm: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        }
        else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        }
        else if (!isPasswordConfirmValid(password, passwordConfirm)) {
            _loginForm.value = LoginFormState(passwordConfirmError = R.string.invalid_password_confirm)
        }
        else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isPasswordConfirmValid(password: String, passwordConfirm: String): Boolean {
        return password.length > 5
    }
}