package com.agileavengers.cooking_recipes_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.agileavengers.cooking_recipes_android.data.model.Category
import com.agileavengers.cooking_recipes_android.ui.subcategory.SubCategoryFragment

class CategoryActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.container)

        // Creates an instance of a sub-category page
        val category = intent.getSerializableExtra("category_data") as? Category
        if (category != null) {
            val fragment = SubCategoryFragment.newInstance(category)
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        }
    }
}