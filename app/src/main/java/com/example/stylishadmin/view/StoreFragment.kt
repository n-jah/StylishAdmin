package com.example.stylishadmin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class StoreFragment : Fragment() {


    private lateinit var brandsAdapter: BrandsAdapter
    private lateinit var itemsAdapter: ItemsAdapter

    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!
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

        setupRecyclerView()
        // Simulate data loading @TODO replace with the real data
        simulateDataLoading()

        // nav to add item fragment
        binding.addProductFAB.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_myStoreFragment_to_addItemFragment)
        }


    }

    private fun setUpSearch() {
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

    private fun filterProducts(quary: String) {


        val filteredList = itemsAdapter.itemList.filter {
            it.title.contains(quary, ignoreCase = true)
        }

        itemsAdapter.updateItems(filteredList)

    }

    private fun setupRecyclerView() {
        brandsAdapter = BrandsAdapter(emptyList(), true) { brand ->
            // Handle brand selection
            println("Selected Brand: ${brand.brandName}")
        }
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

    private fun simulateDataLoading() {
        val fakeList = listOf(
            Brand(
                "All", "https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
                        "/o/2024-08-24%2006-22-09.png?alt=media&token=5e2e3e50-bac4-4192-8ddc-34865d35d9a5"
            ),
            Brand(
                "Nike",
                "https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
                        "/o/2024-08-24%2006-22-09.png?alt=media&token=5e2e3e50-bac4-4192-8ddc-34865d35d9a5"
            ),
            Brand(
                "Adidas",
                "https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
                        "/o/2024-08-24%2006-22-09.png?alt=media&token=5e2e3e50-bac4-4192-8ddc-34865d35d9a5",
            )
        )
        brandsAdapter.updateBrands(fakeList)
        // Simulate loading state
        lifecycleScope.launch {
            delay(1000) // Delay for shimmer
            brandsAdapter.setLoadingState(false)
            brandsAdapter.updateBrands(fakeList)
        }

        val fakeProductList = listOf(
            Item(
                "Nike techear", 51.15, arrayListOf(
                    "https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
                            "/o/2024-08-24%2006-22-09.png?alt=media&token=5e2e3e50-bac4-4192-8ddc-34865d35d9a5"
                ),
                emptyList(), "asdlfsdlkf", "3", 33.3, "Nike"
            ),
            Item(
                "Nike techear", 51.15, arrayListOf(
                    "https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
                            "/o/2024-08-24%2006-22-09.png?alt=media&token=5e2e3e50-bac4-4192-8ddc-34865d35d9a5"
                ),
                emptyList(), "asdlfsdlkf", "3", 33.3, "Nike"
            ),
            Item(
                "Nike techear", 51.15, arrayListOf(
                    "https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
                            "/o/2024-08-24%2006-22-09.png?alt=media&token=5e2e3e50-bac4-4192-8ddc-34865d35d9a5"
                ),
                emptyList(), "asdlfsdlkf", "3", 33.3, "Nike"
            ),
            Item(
                "Nike techear", 51.15, arrayListOf(
                    "https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
                            "/o/2024-08-24%2006-22-09.png?alt=media&token=5e2e3e50-bac4-4192-8ddc-34865d35d9a5"
                ),
                emptyList(), "asdlfsdlkf", "3", 33.3, "Nike"
            ),
            Item(
                "Nike techear", 51.15, arrayListOf(
                    "https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
                            "/o/2024-08-24%2006-22-09.png?alt=media&token=5e2e3e50-bac4-4192-8ddc-34865d35d9a5"
                ),
                emptyList(), "asdlfsdlkf", "3", 33.3, "Nike"
            ),


            )
        itemsAdapter.updateItems(fakeProductList)

        lifecycleScope.launch {
            delay(500)
            itemsAdapter.setLoadingState(false)
            itemsAdapter.updateItems(fakeProductList)
        }
    }


}