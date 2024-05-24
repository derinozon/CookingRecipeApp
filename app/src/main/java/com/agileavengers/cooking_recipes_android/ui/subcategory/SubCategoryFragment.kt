package com.agileavengers.cooking_recipes_android.ui.subcategory

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agileavengers.cooking_recipes_android.R
import com.agileavengers.cooking_recipes_android.databinding.FragmentHomeBinding
import com.agileavengers.cooking_recipes_android.ui.home.GridSpacingItemDecoration
import com.agileavengers.cooking_recipes_android.ui.home.RecipeAdapter
import com.agileavengers.cooking_recipes_android.data.model.Category
import com.agileavengers.cooking_recipes_android.ui.home.HomeViewModel

class SubCategoryFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var viewModel: SubCategoryViewModel


    companion object {
        fun newInstance(category: Category): SubCategoryFragment {
            val fragment = SubCategoryFragment()
            val args = Bundle()
            args.putSerializable("category_data", category)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val rootView = binding.root
        val category = arguments?.getSerializable("category_data") as? Category
        println("All the arguments of current category: $category")
        val categoryName = category?.name
        println("Name of current Category: $categoryName")

        recyclerView = rootView.findViewById(R.id.rvRecipes)
        layoutManager = GridLayoutManager(recyclerView.context, 2)
        adapter = RecipeAdapter()

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        // Set spacing between cards
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.card_spacing)
        recyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = 2,
                spacing = spacingInPixels,
                includeEdge = true
            )
        )

        // implementing scrolling
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    if (lastVisibleItemPosition == totalItemCount - 1) {
                        viewModel.loadMoreData(binding.progressBar, categoryName)
                    }
                }
            }
        })

        viewModel = ViewModelProvider(this).get(SubCategoryViewModel::class.java)
        viewModel.subCategoryList.observe(viewLifecycleOwner, Observer { recipes ->
            adapter.setRecipes(recipes)
        })
        viewModel.loadMoreData(binding.progressBar, categoryName)

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}