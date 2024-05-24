package com.agileavengers.cooking_recipes_android.ui.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.agileavengers.cooking_recipes_android.LoginActivity
import com.agileavengers.cooking_recipes_android.MainActivity
import com.agileavengers.cooking_recipes_android.R
import com.agileavengers.cooking_recipes_android.databinding.FragmentRegisterBinding
import com.agileavengers.cooking_recipes_android.ui.login.LoggedInUserView

class RegisterFragment : Fragment() {

    private lateinit var loginViewModel: RegisterViewModel
    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProvider(this, RegisterViewModelFactory())
            .get(RegisterViewModel::class.java)

        val emailEditText = binding.email
        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val passwordConfirmEditText = binding.passwordConfirm
        val loginButton = binding.login
        val loadingProgressBar = binding.loading
        val backLink = binding.backLink

        backLink.setOnClickListener {
            LoginActivity.loginState = LoginActivity.Companion.STATE.LOGIN
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }

        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
                loginFormState.passwordConfirmError?.let {
                    passwordConfirmEditText.error = getString(it)
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.error?.let {
                    showLoginFailed(it)
                }
                loginResult.success?.let {
                    storeCredentials(it)
                    updateUiWithUser(it)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    emailEditText.text.toString(),
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString(),
                    passwordConfirmEditText.text.toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.register(
                    emailEditText.text.toString(),
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
            false
        }

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel.register(
                emailEditText.text.toString(),
                usernameEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }
    }

    private fun storeCredentials (model: LoggedInUserView) {
        val sharedPreferences = context?.getSharedPreferences("credentials", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString("username", model.displayName)
        editor?.putString("mail", model.displayMail)
        editor?.apply()
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + '\n' + model.displayName
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()

        val intent = Intent(appContext, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}