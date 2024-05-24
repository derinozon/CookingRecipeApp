package com.agileavengers.cooking_recipes_android.ui.categories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agileavengers.cooking_recipes_android.data.model.Category
import com.agileavengers.cooking_recipes_android.networking.RecipeApi
import com.agileavengers.cooking_recipes_android.networking.RetrofitProvider
import kotlinx.coroutines.launch

class CategoriesViewModel : ViewModel() {
    private val recipeApi = RetrofitProvider.retrofit.create(RecipeApi::class.java)

    private val _categoryList = MutableLiveData<List<Category>?>()
    val categoryList: MutableLiveData<List<Category>?> get() = _categoryList

    init {
        loadCategories()
    }
    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val allCategories = recipeApi.getCategories()
                if (allCategories != null) {
                    _categoryList.value = allCategories
                } else {
                    Log.e("CategoriesViewModel", "Error loading categories: Received null response")
                }
            } catch (e: Exception) {
                Log.e("CategoriesViewModel", "Error loading categories: ${e.message}")
            }
        }
    }

}

