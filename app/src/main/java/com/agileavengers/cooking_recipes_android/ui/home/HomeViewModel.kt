package com.agileavengers.cooking_recipes_android.ui.home

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.agileavengers.cooking_recipes_android.data.model.Recipe
import com.agileavengers.cooking_recipes_android.networking.RecipeApi
import com.agileavengers.cooking_recipes_android.networking.RetrofitProvider
import kotlinx.coroutines.launch
import java.util.Locale

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeApi = RetrofitProvider.retrofit.create(RecipeApi::class.java)

    private val _recipeList = MutableLiveData<List<Recipe>>()
    val recipeList: LiveData<List<Recipe>> get() = _recipeList
    private val _selectedFilters = MutableLiveData<Set<String>>()
    val selectedFilters: LiveData<Set<String>> get() = _selectedFilters

    private val selectedFiltersKey = "selected_filters"
    lateinit var progressBar: ProgressBar


    fun initRecipes (context: Context) {
        checkNetworkStatus(context)
        if (networkStatusLiveData.value == true) {
            if (recipeList.value.isNullOrEmpty()) {
                loadRecipes()
                loadSelectedFilters()
            }
        } else {
          // left empty since already handled in the HomeFragment by displaying a Snackbar message -oc16252
        }
    }


    fun setSelectedFilters(filters: Set<String>) {
        _selectedFilters.value = filters
        updateFilteredRecipes()
        saveSelectedFilters()
    }
    private fun updateFilteredRecipes() {
        progressBar.isVisible = true
        viewModelScope.launch {
            val filters = getSelectedFilters().map { it.toLowerCase(Locale.ROOT) }
            val apiCategories =
                recipeApi.getCategories()?.map { it.name.toLowerCase(Locale.ROOT) } ?: emptyList()
            val types = filters.filter { it !in apiCategories }
            val categories = filters.filter { it in apiCategories }
            val filteredRecipes = recipeApi.getRecipes(types, categories, null)
            _recipeList.value = filteredRecipes
        }.invokeOnCompletion {
            progressBar.isVisible = false
        }
    }

    private fun getSelectedFilters(): Set<String> {
        return selectedFilters.value ?: emptySet()
    }

    private var currentPage = 1

    // Loads recipes from backend
    private fun loadRecipes() {
        progressBar.isVisible = true
        viewModelScope.launch {
            try {
                val allRecipes = recipeApi.getRecipes(null, null, null)
                _recipeList.value = allRecipes

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading recipes: ${e.message}")
            }
        }.invokeOnCompletion {
            progressBar.isVisible = false
        }
    }

    // Loads more recipes on demand
    fun loadMoreData() {
        progressBar.isVisible = true
        viewModelScope.launch {
            try {
                val filters = getSelectedFilters().map { it.toLowerCase(Locale.ROOT) }
                val apiCategories =
                    recipeApi.getCategories()?.map { it.name.toLowerCase(Locale.ROOT) }
                        ?: emptyList()
                val types = filters.filter { it !in apiCategories }
                val categories = filters.filter { it in apiCategories }
                val newRecipes = recipeApi.getRecipes(types, categories, null)
                val currentRecipes = _recipeList.value?.toMutableList()
                currentRecipes?.addAll(newRecipes)
                _recipeList.value = currentRecipes!!
                currentPage++
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading more data: ${e.message}")
            }
        }.invokeOnCompletion {
            progressBar.isVisible = false
        }
    }

    val networkStatusLiveData = MutableLiveData<Boolean>()

    // Checks whether the device is connected
    fun checkNetworkStatus(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val isConnected = activeNetwork != null
        networkStatusLiveData.value = isConnected
    }

    // Searches the recipes in backend with the user provided search string
    fun searchRecipes(searchPhrase: String) {
        viewModelScope.launch {
            try {
                val recipes = recipeApi.searchRecipes(searchPhrase)
                _recipeList.value = recipes
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error searching recipes: ${e.message}")
            }
        }
    }


    private fun saveSelectedFilters() {
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences("savedFilters", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet(selectedFiltersKey, getSelectedFilters())
        editor.apply()
    }

    private fun loadSelectedFilters() {
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences("savedFilters", Context.MODE_PRIVATE)
        val savedFilters =
            sharedPreferences.getStringSet(selectedFiltersKey, null)?.toMutableSet()
                ?: mutableSetOf()
        setSelectedFilters(savedFilters)
    }
}