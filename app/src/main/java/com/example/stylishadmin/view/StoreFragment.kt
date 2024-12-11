package com.example.stylishadmin.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stylishadmin.R
import com.example.stylishadmin.adapter.BrandsAdapter
import com.example.stylishadmin.databinding.FragmentStoreBinding
import com.example.stylishadmin.model.brands.Brand
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class StoreFragment : Fragment() {

    private lateinit var brandRecyclerView : RecyclerView
    private lateinit var itemsRecyclerView: RecyclerView
    private lateinit var brandsAdapter : BrandsAdapter

    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreBinding.inflate(inflater , container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    //onViewCreate
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        // Simulate data loading @TODO replace with the real data
        simulateDataLoading()


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

    }

    private fun simulateDataLoading() {
        val fakeList = listOf(
            Brand("All", "https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
                    "/o/2024-08-24%2006-22-09.png?alt=media&token=5e2e3e50-bac4-4192-8ddc-34865d35d9a5"),
            Brand("Nike",
            "https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
                    "/o/2024-08-24%2006-22-09.png?alt=media&token=5e2e3e50-bac4-4192-8ddc-34865d35d9a5") ,
            Brand("Adidas","https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
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
    }


}