package com.agileavengers.cooking_recipes_android.ui.categories

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.agileavengers.cooking_recipes_android.R
import com.agileavengers.cooking_recipes_android.CategoryActivity
import com.agileavengers.cooking_recipes_android.data.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val categoryList = mutableListOf<Category>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.category_card, parent, false)
        val viewHolder = CategoryViewHolder(itemView)

        // OnClick listener for card view
        viewHolder.cardView.setOnClickListener{
            val context = parent.context
            val category =  categoryList[viewHolder.adapterPosition]
            val intent = Intent(context, CategoryActivity:: class.java)
            // Adding any necessary extra data to intent
            intent.putExtra("category_data", category)
            context.startActivity(intent)
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val recipe = categoryList[position]
        holder.bind(recipe)
    }

    fun setCategories(categories: List<Category>) {
        categoryList.clear()
        categoryList.addAll(categories)
        notifyDataSetChanged()
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cvCategory)
        private val categoryImageView: ImageView = itemView.findViewById(R.id.imgCategory)
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.nameCategory)

        fun bind(category: Category) {
            categoryNameTextView.text = category.name

            GlobalScope.launch(Dispatchers.Main) {
                val bitmap = loadImageFromUrl(category.imageURL!!)
                if (bitmap != null) {
                    categoryImageView.setImageBitmap(bitmap)
                }
            }

            val categoryNameTextView : TextView = itemView.findViewById(R.id.nameCategory)
            categoryNameTextView.text = category.name
        }

        // Loads a Bitmap image from given url
        private suspend fun loadImageFromUrl(url: String): Bitmap? = withContext(Dispatchers.IO) {
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