package com.example.stylishadmin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.stylishadmin.databinding.FragmentDashboardBinding
import com.example.stylishadmin.model.orders.Order

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fetch and display recent orders
        fetchAndDisplayRecentOrders()
        // Handle the visibility of the empty view
        handleEmptyView(emptyList())



    }

    private fun fetchAndDisplayRecentOrders() {

    }

    // Function to handle the visibility of the empty view
    private fun handleEmptyView(cartItems: List<Order>) {
        if (cartItems.isEmpty()) {
            binding.recentOrdersRecyclerView.visibility = View.GONE
            binding.emptyRecentOrdersAnimation.visibility = View.VISIBLE  // For Lottie Animation
        } else {
            binding.recentOrdersRecyclerView.visibility = View.VISIBLE
            binding.emptyRecentOrdersAnimation.visibility = View.GONE
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
