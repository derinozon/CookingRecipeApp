package com.agileavengers.cooking_recipes_android.data.model

import java.io.Serializable

data class Category(
    val name: String,
    val imageResId: Int,
    val imageURL : String,
): Serializable {}