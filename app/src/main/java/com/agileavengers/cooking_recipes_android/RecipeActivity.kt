package com.agileavengers.cooking_recipes_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.agileavengers.cooking_recipes_android.data.model.Recipe
import com.agileavengers.cooking_recipes_android.ui.recipe.RecipeFragment

class RecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.container)
        val recipe = intent.getSerializableExtra("recipe_data") as? Recipe
        if (recipe != null) {
            val fragment = RecipeFragment.newInstance(recipe)
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        }
    }
}