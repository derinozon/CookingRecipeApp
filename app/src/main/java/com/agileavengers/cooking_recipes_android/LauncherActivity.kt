package com.agileavengers.cooking_recipes_android

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class LauncherActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.fragment_get_started)
    }

    // Greets the user with a welcome page
    fun GetStarted (view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}