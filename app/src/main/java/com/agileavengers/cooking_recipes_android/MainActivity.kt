
package com.agileavengers.cooking_recipes_android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.agileavengers.cooking_recipes_android.databinding.ActivityMainBinding
import com.agileavengers.cooking_recipes_android.ui.filter.FilterDialogFragment
import com.agileavengers.cooking_recipes_android.ui.home.HomeFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    companion object {
        private const val REQUEST_FILTER = 1
    }

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Gets the settings preferences
        val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)

        // Initialises the dark mode if enabled on settings
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Gets the user credentials, if it doesn't exist  forces the user to login or signup
        val credentials = this.getSharedPreferences("credentials", Context.MODE_PRIVATE)
        val username = credentials?.getString("username", null)
        val mail = credentials?.getString("mail", null)

        if (username == null && mail == null) {
            val intent = Intent(this, LauncherActivity::class.java)
            startActivity(intent)
        }

        // Inflates the layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        // Navigation view
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_settings, R.id.nav_categories
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Updates the header contents
        val headerView = navView.getHeaderView(0)
        val usernameTextView: TextView = headerView.findViewById(R.id.username_field)
        val mailTextView: TextView = headerView.findViewById(R.id.mail_field)

        usernameTextView.text = username
        mailTextView.text = mail
    }
    private fun showFilterDialog() {
        val filterDialog = FilterDialogFragment.newInstance()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val homeFragment = navHostFragment.childFragmentManager.fragments[0] as HomeFragment
        filterDialog.setTargetFragment(homeFragment, REQUEST_FILTER)
        filterDialog.show(navHostFragment.childFragmentManager, "filter_dialog")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.fragments[0]
        if (currentFragment is HomeFragment) {
            menu.findItem(R.id.action_filter).isVisible = true
        } else {
            menu.findItem(R.id.action_filter).isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                showFilterDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val selectedFilters =
                data?.getStringArrayExtra(FilterDialogFragment.EXTRA_SELECTED_FILTERS)?.toSet()
            if (selectedFilters != null) {
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
                val homeFragment = navHostFragment.childFragmentManager.fragments[0] as HomeFragment
                homeFragment.viewModel.setSelectedFilters(selectedFilters)
            }
        }
    }
}
