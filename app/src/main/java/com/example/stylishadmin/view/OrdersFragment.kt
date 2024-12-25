package com.example.stylishadmin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stylishadmin.adapter.OrdersAdapter
import com.example.stylishadmin.databinding.FragmentOrdersBinding
import com.example.stylishadmin.model.items.ItemDetail
import com.example.stylishadmin.model.orders.Order
import com.example.stylishadmin.model.user.UserAddress
import com.example.stylishadmin.repository.orders.OrdersRepoImp
import com.example.stylishadmin.viewModel.orders.OrdersViewModel
import com.example.stylishadmin.viewModel.orders.OrdersViewModelFactory

class OrdersFragment : Fragment() {
    private lateinit var adapter: OrdersAdapter
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var ordersViewModel : OrdersViewModel
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
        //setUpSearch()


    }

    private fun intiObservers() {
        ordersViewModel.orders.observe(viewLifecycleOwner){ orders->
            if(!orders.isNullOrEmpty()){
                binding.emptyAnimation.visibility = View.GONE

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

//    private fun setUpSearch() {
//
//        binding.ordersSearchBar.setOnQueryTextListener(object  : androidx.appcompat.widget.SearchView.OnQueryTextListener,
//            SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                query?.let {
//                    filterProducts(it)
//                }
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//
//                newText?.let {
//                    filterProducts(it)
//                }
//                return true
//
//            }
//
//        })
//
//        }

//    private fun filterProducts(qurey: String) {
//
//
//        val filteredList = fakeListOfOrders.filter {
//            it.orderId.contains(qurey, ignoreCase = true) || it.address.detailedAddress.contains(qurey, ignoreCase = true) || it.orderItems.any { itemDetail ->
//                itemDetail.title.contains(qurey, ignoreCase = true)
//            }
//        }
//        updateOrders(filteredList)
//    }
//
//    private fun updateOrders(filteredList: List<Order>) {
//        adapter.updateOrders(filteredList)
//        adapter.notifyDataSetChanged()
//    }

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

