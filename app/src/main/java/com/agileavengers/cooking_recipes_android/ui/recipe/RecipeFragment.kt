package com.agileavengers.cooking_recipes_android.ui.recipe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.agileavengers.cooking_recipes_android.R
import com.google.android.material.tabs.TabLayout
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.agileavengers.cooking_recipes_android.networking.RecipeApi
import com.agileavengers.cooking_recipes_android.networking.RetrofitProvider
import com.agileavengers.cooking_recipes_android.data.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class RecipeFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var tabText: TextView
    private lateinit var recipeImage: ImageView
    private lateinit var recipeApi: RecipeApi

    private var quantity = 1
    private var imperial = false
    private var screen = 0

    companion object {
        fun newInstance(recipe: Recipe): RecipeFragment {
            val fragment = RecipeFragment()
            val args = Bundle()
            args.putSerializable("recipe_data", recipe)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_recipe, container, false)
        val recipe = arguments?.getSerializable("recipe_data") as? Recipe

        // Cleans the quantity and unit strings and converts the units if needed
        val cleanInput: (String, String) -> String = { a, b ->
            var quantity = (a.trim().toFloat()*quantity)
            var unit = b.trim().lowercase()

            if (imperial) {
                if (unit == "liters") {
                    unit = "cup"
                    quantity /= 0.236588f
                }
                if (unit == "kg" || unit == "kilogram") {
                    unit = "pound"
                    quantity /= 0.453592f
                }
                if (unit == "g" || unit == "gram" || unit == "gramm") {
                    unit = "oz"
                    quantity /= 0.035274f
                }
            }
            else {
                if (unit == "cup") {
                    unit = "liters"
                    quantity *= 0.236588f
                }
                if (unit == "pound") {
                    unit = "kg"
                    quantity *= 0.453592f
                }
                if (unit == "oz" || unit == "ounces") {
                    unit = "g"
                    quantity *= 0.035274f
                }
            }
            val out = String.format("%.2f", quantity)
            "$out $unit"
        }

        // Refreshes either the ingredients or steps text depending on our current screen
        val refreshText = {
            if (screen == 0) {
                tabText.text = recipe?.ingredients?.joinToString(separator = "\n") { "${cleanInput(it.quantity, it.units)} ${it.name}" }
            }
            else {
                tabText.text = "• " + recipe?.steps?.joinToString(separator = "\n• ")
            }
        }

        // Sets up quantity and unit spinners
        val setupSpinners = {
            val sharedPreferences = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
            val useMetricUnits = sharedPreferences.getBoolean("useMetricUnits", false)

            val spinnerQuantity: Spinner = rootView.findViewById(R.id.spinner_quantity)

            spinnerQuantity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    quantity = selectedItem.split(" ")[0].toInt()
                    refreshText()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            val spinnerUnit: Spinner = rootView.findViewById(R.id.spinner_unit)

            spinnerUnit.setSelection(if (useMetricUnits) 0 else 1)
            refreshText()

            spinnerUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    imperial = position == 1
                    refreshText()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        // Sets up ingredients and steps tabs
        val setupTabs = {
            val tabLayout: TabLayout = rootView.findViewById(R.id.tab_layout)

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        when (it.position) {
                            0 -> screen = 0
                            1 -> screen = 1
                        }
                        refreshText()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }

        // Loads the Image from the recipe url and sets up onClick listener for Image Picker
        val setupImageData = {
            GlobalScope.launch(Dispatchers.Main) {
                val bitmap = loadImageFromUrl(recipe?.imageURL!!)
                if (bitmap != null) {
                    recipeImage.setImageBitmap(bitmap)
                }
            }
            recipeImage.setOnClickListener {
                pickImageLauncher.launch("image/*")
            }
        }

        // Sets up the necessary details for the recipe
        val setupRecipeDetails = {
            val recipeName : TextView = rootView.findViewById(R.id.recipeName)
            recipeName.text = recipe?.name
            recipeImage = rootView.findViewById(R.id.recipeImage)
            tabText = rootView.findViewById(R.id.tab_text)
            val userText : TextView = rootView.findViewById(R.id.userText)
            userText.text = recipe?.createdBy?.username
        }

        recipeApi = RetrofitProvider.retrofit.create(RecipeApi::class.java)

        setupRecipeDetails()
        setupImageData()
        setupSpinners()
        setupTabs()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    // Loads the image using http with given url
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

    // Launches the image picker and updates thee recipe image
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            var bitmap: Bitmap = BitmapFactory.decodeStream(
                activity?.contentResolver?.openInputStream(uri)
            )
            bitmap = cropBitmapFromCenter(bitmap)
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)
            recipeImage.setImageBitmap(bitmap)
        }
    }

    // Crops the given bitmap so it fits the recipe frame
    private fun cropBitmapFromCenter(originalBitmap: Bitmap): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height

        var frame = width
        if (width > height)
            frame = height

        val startX = (width - frame) / 2
        val startY = (height - frame) / 2

        return Bitmap.createBitmap(originalBitmap, startX, startY, frame, frame)
    }
}