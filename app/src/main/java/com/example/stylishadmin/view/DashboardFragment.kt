package com.example.stylishadmin.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stylishadmin.adapter.DashboardOrdersAdapter
import com.example.stylishadmin.databinding.FragmentDashboardBinding
import com.example.stylishadmin.model.orders.Order
import com.example.stylishadmin.repository.items.ItemsRepoImp
import com.example.stylishadmin.repository.orders.OrdersRepoImp
import com.example.stylishadmin.repository.users.UserRepoImp
import com.example.stylishadmin.viewModel.items.ItemsViewModel
import com.example.stylishadmin.viewModel.items.ItemsViewModelFactory
import com.example.stylishadmin.viewModel.orders.OrdersViewModel
import com.example.stylishadmin.viewModel.orders.OrdersViewModelFactory
import com.example.stylishadmin.viewModel.users.UserViewModelFactory
import com.example.stylishadmin.viewModel.users.UsersViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!


    // viewmodel
    private lateinit var ordersViewModel : OrdersViewModel
    private  lateinit var itemsViewModel : ItemsViewModel
    private lateinit var usersViewModel: UsersViewModel


    private  lateinit var  ordersAdapter: DashboardOrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //start viewmodel
        initViewModel()
        // Fetch and display recent orders
        setupRecyclerView()
        // Fetch data and update UI
        fetchInitialData()
        // init observer
        initObservers()

        // Set up the pie chart statistics
        brandsStatistics()
        ordersStatistics()
        //topPart statistics


    }


    private fun initViewModel() {

        //orders
        val ordersFactory = OrdersViewModelFactory(OrdersRepoImp())
        ordersViewModel = ViewModelProvider(requireActivity(), ordersFactory)[OrdersViewModel::class.java]

        //items
        val itemsFactory = ItemsViewModelFactory(ItemsRepoImp())
        itemsViewModel = ViewModelProvider(requireActivity(), itemsFactory)[ItemsViewModel::class.java]

        //users
        val usersFactory = UserViewModelFactory(UserRepoImp())
        usersViewModel = ViewModelProvider(requireActivity(),usersFactory)[UsersViewModel::class.java]


    }

    private fun setupRecyclerView() {

        ordersAdapter = DashboardOrdersAdapter(emptyList())
        binding.recentOrdersList.adapter = ordersAdapter
        binding.recentOrdersList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }

    private fun initObservers() {

        //loading
        ordersViewModel.loading.observe(viewLifecycleOwner, Observer {
            setProgressBarVisibility(it)
        })

        // orders
        ordersViewModel.orders.observe(viewLifecycleOwner) { orders ->
            ordersAdapter.updateOrders(orders?.reversed())
            if (orders != null) {
                handleEmptyView(orders)
            }
        }

        // users

        usersViewModel.users.observe(viewLifecycleOwner) { users ->
            binding.totalCustomersTv.text = users?.size?.toString() ?: "0"
        }

        //get total products
        itemsViewModel.items.observe(viewLifecycleOwner) { items ->
            binding.totalProductsTv.text = items?.size?.toString() ?: "0"
        }

    }
    private fun fetchInitialData() {
        ordersViewModel.getOrders()
        itemsViewModel.getItems()
        usersViewModel.getTotalUsers()

    }
    private fun ordersStatistics() {
        setupPieChart(binding.ordersPieChart, mapOf("" to 0))
        ordersViewModel.getStatistics { statistics ->
            if (statistics.isSuccess) {
                val ordersData = statistics.getOrNull()
                if (ordersData != null) {
                    setupPieChart(binding.ordersPieChart, ordersData)
                }
            }
        }

    }
    private fun brandsStatistics() {
        var totalBrands : Int? = 0
       setupPieChart(binding.itemsPieChart, mapOf("" to 0))
        itemsViewModel.getItemsStatistics { items ->
            if (items.isSuccess){
                val itemsData = items.getOrNull()
                if (itemsData != null) {
                    setupPieChart(binding.itemsPieChart, itemsData)
                    totalBrands = itemsData.size
                    binding.totalBrandsTv.text = totalBrands.toString()
                }
            }
        }
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
