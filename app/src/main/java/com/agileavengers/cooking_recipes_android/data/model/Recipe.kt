package com.agileavengers.cooking_recipes_android.data.model

import java.io.Serializable

data class Recipe(
    val name: String,
    val imageResId: Int,

    val ingredients: List<Ingredient>?,
    val steps: List<String>?,
    val timers: List<Int>?,
    val imageURL: String?,
    val types: List<Type>,
    val categories: List<Category>,

    val createdBy: User
): Serializable {}

data class Ingredient(
    val quantity: String,
    val name: String,
    val units: String
): Serializable {}