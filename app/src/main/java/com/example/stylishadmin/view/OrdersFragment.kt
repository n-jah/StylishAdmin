package com.example.stylishadmin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stylishadmin.adapter.OrdersAdapter
import com.example.stylishadmin.databinding.FragmentOrdersBinding
import com.example.stylishadmin.model.items.ItemDetail
import com.example.stylishadmin.model.orders.Order
import com.example.stylishadmin.model.user.UserAddress
import com.example.stylishadmin.repository.orders.OrdersRepoImp
import com.example.stylishadmin.viewModel.orders.OrdersViewModel
import com.example.stylishadmin.viewModel.orders.OrdersViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {
    private lateinit var adapter: OrdersAdapter
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var ordersViewModel : OrdersViewModel
    private var allOrders: List<Order> = emptyList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
    }

    private fun setUpUI() {


        initViewModels()


        //setUp the rv
        setupRecyclerView()

        //observers
        intiObservers()

        // search
        setUpSearch()

        binding.swipfreshlayout.setOnRefreshListener {
            refreshData()
        }

    }
    private fun refreshData() {

        lifecycleScope.launch {
            ordersViewModel.getOrders()
            delay(1000)
            binding.swipfreshlayout.isRefreshing = false
        }

    }
    private fun intiObservers() {
        ordersViewModel.orders.observe(viewLifecycleOwner){ orders->
            if(!orders.isNullOrEmpty()){
                binding.emptyAnimation.visibility = View.GONE
                allOrders = orders // Save all orders for filtering

                adapter.updateOrders(orders)

            }else{
                //show empty state
                binding.emptyAnimation.visibility = View.VISIBLE

            }
        }

        //observe the loading state
        ordersViewModel.loading.observe(viewLifecycleOwner){ isLoading->
            if (isLoading) {
                binding.ordersProgressBar.visibility = View.VISIBLE
            }else{
                binding.ordersProgressBar.visibility = View.GONE
            }
        }

    }

    private fun initViewModels() {
        val ordersFactory = OrdersViewModelFactory(OrdersRepoImp())
        ordersViewModel = ViewModelProvider(this, ordersFactory)[OrdersViewModel::class.java]

    }

    private fun setUpSearch() {
        binding.ordersSearchBar.setOnClickListener { binding.ordersSearchBar.isIconified = false }

        binding.ordersSearchBar.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener,
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterOrders(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterOrders(it) }
                return true
            }
        })
    }

    private fun filterOrders(query: String) {
        val filteredList = allOrders.filter { order ->
            order.orderId.contains(query, ignoreCase = true) ||
                    order.address.detailedAddress.contains(query, ignoreCase = true) ||
                    order.orderItems.any { item -> item.title.contains(query, ignoreCase = true) }
        }
        adapter.updateOrders(filteredList)
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.orderItemsRecyclerview

        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )

        adapter = OrdersAdapter(emptyList()) { order, newStatus ->
            updateOrderStatus(order, newStatus)
        }
        recyclerView.adapter = adapter

        fetchOrders()

    }



    private fun updateOrderStatus(order: Order, newStatus: String) {
        // Update the order status in the database
        ordersViewModel.setOrderState(order.orderId, newStatus)
        ordersViewModel.getOrders()



    }


    private fun fetchOrders() {

    }
}

