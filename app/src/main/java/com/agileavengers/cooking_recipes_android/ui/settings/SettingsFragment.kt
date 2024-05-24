package com.agileavengers.cooking_recipes_android.ui.settings

import android.app.UiModeManager
import android.app.UiModeManager.MODE_NIGHT_YES
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.agileavengers.cooking_recipes_android.LauncherActivity
import com.agileavengers.cooking_recipes_android.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Access the SharedPreferences in the Fragment's onCreateView() method
        val sharedPreferences = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)

        val unitsSwitch = binding.unitsSwitch
        val darkModeSwitch = binding.darkModeSwitch

        // Retrieve the saved state of the switches from SharedPreferences
        val useMetricUnits = sharedPreferences.getBoolean("useMetricUnits", false)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)

        // Set the initial state of the switches
        unitsSwitch.isChecked = useMetricUnits
        darkModeSwitch.isChecked = isDarkMode

        // Handle the Metric/Imperial Units Switch
        unitsSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the state of the switch in SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putBoolean("useMetricUnits", isChecked)
            editor.apply()
        }

        // Handle the Dark Mode Switch
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the state of the switch in SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putBoolean("isDarkMode", isChecked)
            editor.apply()

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val logoutButton : Button = binding.logoutButton;
        logoutButton.setOnClickListener {
            val credentials = requireActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE)
            val editor = credentials.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(requireContext(), LauncherActivity::class.java)
            startActivity(intent)
        }

        return root
    }
    override fun onResume() {
        super.onResume()
        requireActivity().invalidateOptionsMenu()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}