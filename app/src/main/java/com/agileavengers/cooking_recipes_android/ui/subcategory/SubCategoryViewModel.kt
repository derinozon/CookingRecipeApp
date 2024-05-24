package com.agileavengers.cooking_recipes_android.ui.subcategory

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agileavengers.cooking_recipes_android.data.model.Recipe
import com.agileavengers.cooking_recipes_android.networking.RecipeApi
import com.agileavengers.cooking_recipes_android.networking.RetrofitProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubCategoryViewModel : ViewModel() {
    private val recipeApi = RetrofitProvider.retrofit.create(RecipeApi::class.java)

    private val _subCategoryList = MutableLiveData<List<Recipe>>()
    val subCategoryList: LiveData<List<Recipe>> get() = _subCategoryList
    private var currentPage = 1

    init {
        _subCategoryList.value = mutableListOf()
    }

    fun loadMoreData(progressBar: ProgressBar, categoryName: String?) {
        viewModelScope.launch {
            try {
                val newRecipes = getRecipes()
                val currentRecipes = _subCategoryList.value?.toMutableList()
                println("Looking for: $categoryName")

                val filteredRecipes = mutableListOf<Recipe>()
                for (recipe in newRecipes) {
                    if(recipe.categories.any{ category -> category.name == categoryName }) {
                        filteredRecipes.add(recipe)
                    }
                }
                currentRecipes?.addAll(filteredRecipes)
                _subCategoryList.value = currentRecipes!!
                currentPage++
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading more data: ${e.message}")
            }
        }.invokeOnCompletion {
            progressBar.isVisible = false
        }
    }

    suspend fun getRecipes(): List<Recipe> {
        return withContext(Dispatchers.IO) {
            recipeApi.getRecipes(null, null, null)
        }
    }

    val networkStatusLiveData = MutableLiveData<Boolean>()

    fun checkNetworkStatus(context: Context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val isConnected = activeNetwork != null
        networkStatusLiveData.value = isConnected
    }
}