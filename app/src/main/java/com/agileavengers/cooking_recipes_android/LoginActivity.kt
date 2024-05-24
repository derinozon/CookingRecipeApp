package com.agileavengers.cooking_recipes_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.agileavengers.cooking_recipes_android.ui.login.LoginFragment
import com.agileavengers.cooking_recipes_android.ui.register.RegisterFragment

class LoginActivity : AppCompatActivity(){
    companion object {
        enum class STATE {
            LOGIN,
            SIGNUP
        }

        var loginState = STATE.LOGIN
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Logs-in or registers depending on the state
        if (loginState == STATE.LOGIN) {
            setContentView(R.layout.container)
            supportFragmentManager.beginTransaction().replace(R.id.container, LoginFragment()).commit()
        }
        else {
            setContentView(R.layout.container)
            supportFragmentManager.beginTransaction().replace(R.id.container, RegisterFragment()).commit()
        }
    }

}