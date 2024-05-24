package com.agileavengers.cooking_recipes_android.ui.filter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class FilterDialogFragment : DialogFragment() {
    private val filterOptions = arrayOf(
        "Breakfast",
        "Lunch",
        "Dinner",
        "Snack",
        "Finger food",
        "Cake",
        "Drink",
        "Meat",
        "Meat-less",
        "Vegetarian",
        "Vegan",
        "Soup",
        "Salad",
        "Meal accompaniment",
        "Alcohol"
    )
    private lateinit var checkedItems: BooleanArray
    private lateinit var selectedFilters: MutableSet<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkedItems = BooleanArray(filterOptions.size)
        loadSelectedFilters()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Filter Recipes")
            .setMultiChoiceItems(filterOptions, checkedItems) { _, which, isChecked ->
                if (isChecked) {
                    selectedFilters.add(filterOptions[which])
                } else {
                    selectedFilters.remove(filterOptions[which])
                }
            }
            .setPositiveButton("Apply") { _, _ ->
                saveSelectedFilters()
                targetFragment?.onActivityResult(
                    targetRequestCode,
                    Activity.RESULT_OK,
                    Intent().putExtra(EXTRA_SELECTED_FILTERS, selectedFilters.toTypedArray())
                )
            }
            .setNegativeButton("Cancel", null)
            .create()

        return dialog
    }

    private fun loadSelectedFilters() {
        val sharedPreferences = requireActivity().getSharedPreferences("savedFilters", Context.MODE_PRIVATE)
        val savedFilters = sharedPreferences.getStringSet("selectedFiltersKey", null)?.toMutableSet() ?: mutableSetOf()
        selectedFilters = savedFilters
        for (i in filterOptions.indices) {
            checkedItems[i] = selectedFilters.contains(filterOptions[i])
        }
    }

    private fun saveSelectedFilters() {
        val sharedPreferences = requireActivity().getSharedPreferences("savedFilters", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("selectedFiltersKey", selectedFilters)
        editor.apply()
    }

    companion object {
        const val EXTRA_SELECTED_FILTERS = "selected_filters"

        fun newInstance(): FilterDialogFragment{
            return FilterDialogFragment()
        }
    }
}
