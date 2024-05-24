package com.agileavengers.cooking_recipes_android.ui.home

import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.agileavengers.cooking_recipes_android.R
import com.agileavengers.cooking_recipes_android.RecipeActivity
import com.agileavengers.cooking_recipes_android.data.model.Recipe
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private val recipeList = mutableListOf<Recipe>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.recipe_card, parent, false)
        val viewHolder = RecipeViewHolder(itemView)

        // set click listener to card view
        viewHolder.cardView.setOnClickListener {
            val context = parent.context
            val recipe = recipeList[viewHolder.adapterPosition]
            val intent = Intent(context, RecipeActivity::class.java)
            intent.putExtra("recipe_data", recipe)
            context.startActivity(intent)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    // Restructures recipe list and notifies it
    fun setRecipes(recipes: List<Recipe>) {
        recipeList.clear()
        recipeList.addAll(recipes)
        notifyDataSetChanged()
    }

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cvRecipe)
        private val recipeImageView: ImageView = itemView.findViewById(R.id.imgRecipe)
        private val recipeNameTextView: TextView = itemView.findViewById(R.id.nameRecipe)

        fun bind(recipe: Recipe) {
            recipeNameTextView.text = recipe.name

            GlobalScope.launch(Dispatchers.Main) {
                if (recipe.imageURL != null) {
                    val bitmap = loadImageFromUrl(recipe.imageURL)
                    if (bitmap != null) {
                        recipeImageView.setImageBitmap(bitmap)
                    }
                }
            }

            val recipeNameTextView : TextView = itemView.findViewById(R.id.nameUser)
            recipeNameTextView.text = recipe.createdBy.username
        }

        // Loads a Bitmap Image given a url
        suspend fun loadImageFromUrl(url: String): Bitmap? = withContext(Dispatchers.IO) {
            var bitmap: Bitmap? = null
            try {
                val urlConnection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
                urlConnection.connect()
                val inputStream: InputStream = urlConnection.inputStream
                bitmap = BitmapFactory.decodeStream(inputStream)
                urlConnection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext bitmap
        }
    }
}
