package com.agileavengers.cooking_recipes_android.ui.categories

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agileavengers.cooking_recipes_android.MainActivity
import com.agileavengers.cooking_recipes_android.R
import com.agileavengers.cooking_recipes_android.databinding.FragmentCategoriesBinding
import com.agileavengers.cooking_recipes_android.ui.filter.FilterDialogFragment
import com.agileavengers.cooking_recipes_android.ui.home.GridSpacingItemDecoration

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var viewModel: CategoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val rootView = binding.root

        recyclerView = rootView.findViewById(R.id.rvCategories)
        layoutManager = GridLayoutManager(recyclerView.context, 2)
        adapter = CategoryAdapter()

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

        viewModel = ViewModelProvider(this).get(CategoriesViewModel::class.java)
        viewModel.categoryList.observe(viewLifecycleOwner, Observer { categories ->
            if (categories != null) {
                adapter.setCategories(categories)
            }
        })

        return rootView
    }
    override fun onResume() {
        super.onResume()
        requireActivity().invalidateOptionsMenu()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
