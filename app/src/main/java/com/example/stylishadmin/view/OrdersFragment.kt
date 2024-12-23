package com.example.stylishadmin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stylishadmin.adapter.OrdersAdapter
import com.example.stylishadmin.databinding.FragmentOrdersBinding
import com.example.stylishadmin.model.items.ItemDetail
import com.example.stylishadmin.model.orders.Order
import com.example.stylishadmin.model.user.UserAddress

class OrdersFragment : Fragment() {
    private lateinit var adapter: OrdersAdapter
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    var fakeListOfOrders = mutableListOf(
        Order(
            "2232", listOf(
                ItemDetail(
                    "32e4", "nestedItem", 23.34f, listOf(
                        "https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
                                "/o/2024-08-24%2006-22-09.png?alt=media&token=5e2e3e50-bac4-4192-8ddc-34865d35d9a5"

                    ), 6, "s", false
                ),ItemDetail(
                    "3243", "nestedItem", 23.34f, listOf(
                        "https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
                                "/o/2024-08-24%2006-22-09.png?alt=media&token=5e2e3e50-bac4-4192-8ddc-34865d35d9a5"

                    ), 6, "s", false
                ),ItemDetail(
                    "324", "nestedItem", 23.34f, listOf(
                        "https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
                                "/o/2024-08-24%2006-22-09.png?alt=media&token=5e2e3e50-bac4-4192-8ddc-34865d35d9a5"

                    ), 6, "s", false
                ),
            ),"nagah 33","23423","32",UserAddress("ahmed", detailedAddress ="adsff"),"pending"

        ),

        Order(
            "223332",
            listOf(
                ItemDetail(
                    "324", "nestedItem", 23.34f, listOf(
                        "https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
                                "/o/2024-08-24%2006-22-09.png?alt=media&token=5e2e3e50-bac4-4192-8ddc-34865d35d9a5"

                    ), 6, "s", false
                )


            ),
            "ahmedId",
            "324324/32",
            "200",
            UserAddress("ahem", "egy", "sfsd", "dsflsdf", "32234"),
            "pending"
        ),
        Order(
            "2231",
            emptyList(),
            "ahmedId",
            "324324/32",
            "200",
            UserAddress("ahem", "egy", "sfsd", "dsflsdf", "32234"),
            "pending"
        ),
        Order(
            "22389",
            emptyList(),
            "ahmedId",
            "324324/32",
            "200",
            UserAddress("ahem", "egy", "sfsd", "dsflsdf", "32234"),
            "pending"
        ),
        Order(
            "22325",
            emptyList(),
            "ahmedId",
            "324324/32",
            "200",
            UserAddress("ahem", "egy", "sfsd", "dsflsdf", "32234"),
            "pending"
        )
    )
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


        //setUp the rv
        setupRecyclerView()

        // search
        setUpSearch()


    }

    private fun setUpSearch() {

        binding.ordersSearchBar.setOnQueryTextListener(object  : androidx.appcompat.widget.SearchView.OnQueryTextListener,
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

        })    }

    private fun filterProducts(qurey: String) {


        val filteredList = fakeListOfOrders.filter {
            it.orderId.contains(qurey, ignoreCase = true) || it.address.detailedAddress.contains(qurey, ignoreCase = true) || it.orderItems.any { itemDetail ->
                itemDetail.title.contains(qurey, ignoreCase = true)
            }
        }
        updateOrders(filteredList)
    }

    private fun updateOrders(filteredList: List<Order>) {
        adapter.updateOrders(filteredList)
        adapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.orderItemsRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )

        adapter = OrdersAdapter(fakeListOfOrders) { order, newStatus ->
            updateOrderStatus(order, newStatus)
        }
        recyclerView.adapter = adapter

        fetchOrders()

    }



    private fun updateOrderStatus(order: Order, newStatus: String) {
        // Find the index of the order in the list
        val index = fakeListOfOrders.indexOfFirst { it.orderId == order.orderId }

        if (index != -1) {
            // Update the list with a new Order instance (data class requires immutability)
            val updatedOrder = fakeListOfOrders[index].copy(status = newStatus)
            fakeListOfOrders[index] = updatedOrder

            // Notify the adapter of the change
            adapter.updateOrders(fakeListOfOrders.toList())
            adapter.notifyItemChanged(index)
        }
    }


    private fun fetchOrders() {

    }
}

