package com.agileavengers.cooking_recipes_android.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agileavengers.cooking_recipes_android.R
import com.agileavengers.cooking_recipes_android.databinding.FragmentHomeBinding
import com.agileavengers.cooking_recipes_android.ui.filter.FilterDialogFragment
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private lateinit var layoutManager: LinearLayoutManager
    lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val rootView = binding.root

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

        // OnScroll listener to load more recipes on demand
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    if (lastVisibleItemPosition == totalItemCount - 1) {
                        viewModel.loadMoreData()
                    }
                }
            }
        })

        // Initialising the ViewModel and setting the necessary variables and listeners
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.progressBar = binding.progressBar
        viewModel.initRecipes(requireContext())

        viewModel.recipeList.observe(viewLifecycleOwner) { recipes ->
            adapter.setRecipes(recipes)
        }

        viewModel.selectedFilters.observe(viewLifecycleOwner) { filters ->
            val filteredRecipes = viewModel.recipeList.value ?: emptyList()
            adapter.setRecipes(filteredRecipes)
        }

        viewModel.networkStatusLiveData.observe(viewLifecycleOwner) { isConnected ->
            if (!isConnected) {
                val snackbar = Snackbar.make(
                        binding.root,
                        "Please connect to the internet!",
                        Snackbar.LENGTH_INDEFINITE
                    )
                snackbar.setAction("Connect") {
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(intent)
                }
                snackbar.show()
            }
        }

        val searchView = rootView.findViewById<SearchView>(R.id.searchView)

        searchView.setOnClickListener {
            searchView.isIconified = false
        }

        // Scroll the NestedScrollView to the top when the SearchView gains focus
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val nestedScrollView = rootView.findViewById<NestedScrollView>(R.id.nsvRecipe)
                nestedScrollView.smoothScrollTo(0, 0)
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchRecipes(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchRecipes(newText ?: "")
                return true
            }
        })

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.main, menu)
        val filterItem = menu.findItem(R.id.action_filter)
        filterItem.setOnMenuItemClickListener {

            val filterDialog = FilterDialogFragment.newInstance()
            filterDialog.setTargetFragment(this, REQUEST_FILTER)
            filterDialog.show(parentFragmentManager, "filter_dialog")
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_FILTER && resultCode == Activity.RESULT_OK) {

            val selectedFilters = data?.getStringArrayExtra(FilterDialogFragment.EXTRA_SELECTED_FILTERS)?.toSet()

            if (selectedFilters != null) {

                viewModel.setSelectedFilters(selectedFilters)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.initRecipes(requireContext())
        requireActivity().invalidateOptionsMenu()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        private const val REQUEST_FILTER = 1
    }
}

