package com.example.stylishadmin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stylishadmin.R
import com.example.stylishadmin.adapter.BrandsAdapter
import com.example.stylishadmin.adapter.ItemsAdapter
import com.example.stylishadmin.databinding.FragmentStoreBinding
import com.example.stylishadmin.model.brands.Brand
import com.example.stylishadmin.model.items.Item
import com.example.stylishadmin.model.items.Size
import com.example.stylishadmin.model.orders.Order
import com.example.stylishadmin.repository.brands.BrandsRepoImpl
import com.example.stylishadmin.repository.items.ItemsRepoImp
import com.example.stylishadmin.viewModel.brands.BrandsViewModel
import com.example.stylishadmin.viewModel.brands.BrandsViewModelFactory
import com.example.stylishadmin.viewModel.items.ItemsViewModel
import com.example.stylishadmin.viewModel.items.ItemsViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class StoreFragment : Fragment() {


    private lateinit var brandsAdapter: BrandsAdapter
    private lateinit var itemsAdapter: ItemsAdapter

    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var itemsViewModel: ItemsViewModel
    private lateinit var brandsViewModel: BrandsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    //onViewCreate
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setupUI
        setUpUI()

    }

    private fun setUpUI() {

        // search
        setUpSearch()

        // setup viewModels
        setupViewModels()

        setupRecyclerView()
        //setup observers
        setupObservers()

        // nav to add item fragment
        binding.addProductFAB.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_myStoreFragment_to_addItemFragment)
        }
        binding.swipfreshlayout.setOnRefreshListener {
            refreshData()
        }



    }

    private fun refreshData() {

        lifecycleScope.launch {
            itemsViewModel.getItems()
            brandsViewModel.getBrands()
            delay(1000)
            binding.swipfreshlayout.isRefreshing = false
        }

    }

    private fun setupObservers() {

        //brands
        this.brandsViewModel.brands.observe(viewLifecycleOwner) { brands ->
            brands?.let {
                brandsAdapter.updateBrands(it)

            }
        }

        brandsViewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                brandsAdapter.setLoadingState(true)
            } else {
                brandsAdapter.setLoadingState(false)
            }
        }

        //items
        itemsViewModel.items.observe(viewLifecycleOwner) { items ->
            items?.let {
                itemsAdapter.updateItems(it)
            }
        }

        itemsViewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                itemsAdapter.setLoadingState(true)

            } else {
                itemsAdapter.setLoadingState(false)
            }
        }


    }

    private fun setupViewModels() {
        val itemsViewModelFactory = ItemsViewModelFactory(ItemsRepoImp())
        itemsViewModel = ViewModelProvider(requireActivity(),itemsViewModelFactory)[ItemsViewModel::class.java]
        //brands
        val brandsViewModelFactory = BrandsViewModelFactory(BrandsRepoImpl())
        brandsViewModel = ViewModelProvider(requireActivity(),brandsViewModelFactory)[BrandsViewModel::class.java]

        itemsViewModel.getItems()
        brandsViewModel.getBrands()
    }

    private fun setUpSearch() {
        binding.productSearchBar.setOnClickListener { binding.productSearchBar.isIconified = false }
        binding.productSearchBar.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener,
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    filterProducts(it)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterProducts(it)
                }
                return true
            }
        })


    }
    private fun filterProducts(query: String) {
        val originalList = itemsViewModel.items.value ?: emptyList()
        if (query.isEmpty()) {
            // Reset the adapter to show the full list
            itemsAdapter.updateItems(originalList)
        } else {
            // Filter the list based on the query
            val filteredList = originalList.filter {
                it.title.contains(query, ignoreCase = true)
            }
            itemsAdapter.updateItems(filteredList)
        }
    }
    private fun setupRecyclerView() {
        brandsAdapter = BrandsAdapter(emptyList(), true, { brand ->

            if (brand.brandName == "All") {
                itemsViewModel.getItems()
                return@BrandsAdapter
            }
            // Handle brand selection
            val filterdList =   itemsViewModel.items.value?.filter { item ->
                item.brand.lowercase() == brand.brandName.lowercase()
            }

            itemsAdapter.updateItems(

                filterdList ?: emptyList()
            )
        }, onManageClicked = {
            brandsViewModel.brands.value?.let { brands ->
                val dialog = BrandDialogFragment.newInstance(brands)
                dialog.show(childFragmentManager, "ManageBrandsDialogFragment")
            }

        })

        binding.brandsRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )
        binding.brandsRecyclerView.adapter = brandsAdapter


        // items
        itemsAdapter = ItemsAdapter(emptyList(), true) { item ->

            val action = StoreFragmentDirections.actionMyStoreFragmentToEditItemFragment(item)
            findNavController().navigate(action)

        }
        binding.productsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.productsRecyclerView.adapter = itemsAdapter

    }




}