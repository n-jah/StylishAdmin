package com.example.stylishadmin.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.stylishadmin.R
import com.example.stylishadmin.databinding.FragmentDashboardBinding
import com.example.stylishadmin.model.orders.Order
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

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


        val userPieChart = view.findViewById<PieChart>(R.id.usersPieChart)
        val itemPieChart = view.findViewById<PieChart>(R.id.itemsPieChart)

        val userData = mapOf("Pending Orders" to 30, "Completed Orders" to 60)
        val itemData = mapOf("Nike" to 30, "Puma" to 50, "Addidas" to 10, "Other " to 20)

        // Set up the pie charts
        setupPieChart(userPieChart, userData)
        setupPieChart(itemPieChart, itemData)


    }


    private fun setupPieChart(pieChart: PieChart, data: Map<String, Int>) {
        // Check if data is empty or null
        if (data.isEmpty()) {
            pieChart.visibility = View.GONE  // Hide the chart if no data
            return
        }

        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()

        // Use a method to get the colors based on the number of items
        val colorPalette = getColorPalette(data.size)

        // Add pie chart entries and colors
        data.toList().forEachIndexed { index, (key, value) ->
            entries.add(PieEntry(value.toFloat(), key))
            colors.add(colorPalette[index % colorPalette.size])  // Cycle through color palette
        }

        // Create PieDataSet and configure appearance
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE  // Set value text color to white

        // Create PieData and assign to PieChart
        val pieData = PieData(dataSet)
        pieChart.data = pieData

        // Chart configuration
        pieChart.description.isEnabled = false  // Disable default description
        pieChart.setUsePercentValues(true)  // Show percentage values
        pieChart.isDrawHoleEnabled = true  // Enable the hole in the center
        pieChart.holeRadius = 15f  // Hole radius
        pieChart.setHoleColor(Color.TRANSPARENT)  // Set hole color to transparent
        pieChart.setDrawCenterText(false)  // Disable center text
        pieChart.setEntryLabelColor(Color.WHITE)  // Set label text color to white
        pieChart.setEntryLabelTextSize(12f)  // Increase label text size for readability

        // Add animation for better interaction
        pieChart.animateY(1000)

        // Refresh the PieChart
        pieChart.invalidate()

        // Hide legend if not necessary
        pieChart.legend.isEnabled = false
    }

    // Method to return a color palette based on the number of data points
    private fun getColorPalette(size: Int): List<Int> {
        // Define a color palette to handle different sizes dynamically
        val colors = when (size) {
            1 -> listOf(Color.parseColor("#FF6500"))  // One section - Bright Orange
            2 -> listOf(Color.parseColor("#FF6500"), Color.parseColor("#1E3E62"))  // Two sections
            3 -> listOf(Color.parseColor("#FF6500"), Color.parseColor("#1E3E62"), Color.parseColor("#0B192C"))  // Three sections
            else -> listOf(
                Color.parseColor("#FF6500"),  // Bright Orange
                Color.parseColor("#1E3E62"),  // Dark Blue
                Color.parseColor("#0B192C"),  // Very Dark Blue/Black
                Color.parseColor("#000000")   // Black
            )  // Default palette
        }
        return colors
    }


    private fun fetchAndDisplayRecentOrders() {

    }

    // Function to handle the visibility of the empty view
    private fun handleEmptyView(cartItems: List<Order>) {
        if (cartItems.isEmpty()) {
            binding.recentOrdersList.visibility = View.GONE
            binding.emptyAnimation.visibility = View.VISIBLE  // For Lottie Animation
        } else {
            binding.recentOrdersLabel.visibility = View.VISIBLE
            binding.emptyAnimation.visibility = View.GONE
        }
    }

    //progress bar
    private fun setProgressBarVisibility(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.recentOrdersList.visibility = if (isVisible) View.GONE else View.VISIBLE
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
