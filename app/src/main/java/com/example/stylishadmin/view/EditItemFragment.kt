package com.example.stylishadmin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stylishadmin.adapter.ImagesAdapter
import com.example.stylishadmin.adapter.SizeStockAdapter
import com.example.stylishadmin.databinding.FragmentEditItemBinding
import com.example.stylishadmin.model.brands.Brand
import com.example.stylishadmin.model.items.Item
import com.example.stylishadmin.model.items.Size

class EditItemFragment : Fragment() {

    private var _binding: FragmentEditItemBinding? = null
    private val binding get() = _binding!!
    private lateinit var brandAdapter: ArrayAdapter<String>
    private var brandList: MutableList<Brand> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup UI with data
        setupUI()
    }

    private fun setupUI() {
        val args = EditItemFragmentArgs.fromBundle(requireArguments())
        val item = args.itemData

        // Pre-fill item details
        binding.itemNameInputText.setText(item.title)
        binding.priceInputText.setText(item.price.toString())
        binding.productDescription.setText(item.description)

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        setUpRVSizeandImages(item)

        // Setup Brand Spinner
        setupBrandSpinner(item.brand)

        // Save Button Click
        binding.saveProductButton.setOnClickListener {
            saveItem(item)
        }
    }

    private fun setUpRVSizeandImages(item : Item) {
        //get sizes to mutable list of pair <string , int>
        val sizes = item.sizes.map { it.size to it.stock }.toMutableList()

        binding.sizeStockRv.adapter = SizeStockAdapter(sizes, onEditClick = {
            val dialog = ManageSizeDialogFragment.newInstance(sizes.map { Size(it.first,it.second) })
            dialog.show(childFragmentManager, "ManageSizeDialogFragment")
        }

        )


        binding.sizeStockRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.sizeStockRv.setHasFixedSize(true)

        binding.itemImagesRv.adapter = ImagesAdapter(item.imgUrl.toMutableList(), onRemoveImageClick = {
            Toast.makeText(requireContext(),"remove this image ",Toast.LENGTH_SHORT).show()
            val index = item.imgUrl.indexOf(it)
            if (index >= 0) {
                item.imgUrl.removeAt(index)
                binding.itemImagesRv.adapter?.notifyItemRemoved(index)
            }
        }, onAddClick = {
            Toast.makeText(requireContext(),"add images", Toast.LENGTH_SHORT).show()

        })
        binding.itemImagesRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.itemImagesRv.setHasFixedSize(true)


    }

    private fun setupBrandSpinner(selectedBrand: String) {
        // Create Adapter for Spinner
        brandAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.brandDropdown.adapter = brandAdapter

        // Simulate loading brand data
        loadBrands(selectedBrand)
    }

    private fun loadBrands(selectedBrand: String) {
        // Simulate data loading
        brandList.apply {
            add(Brand("Adidas", "https://example.com/adidas-logo.png"))
            add(Brand("Nike", "https://example.com/nike-logo.png"))
            add(Brand("Puma", "https://example.com/puma-logo.png"))
        }

        // Update Spinner Adapter
        brandAdapter.clear()
        brandAdapter.addAll(brandList.map { it.brandName })

        // Set selected brand
        val selectedIndex = brandList.indexOfFirst { it.brandName == selectedBrand }
        if (selectedIndex >= 0) {
            binding.brandDropdown.setSelection(selectedIndex)
        }
    }

    private fun saveItem(item: Item) {
        val selectedBrand = brandList[binding.brandDropdown.selectedItemPosition].brandName
        val updatedItem = item.copy(
            title = binding.itemNameInputText.text.toString(),
            price = binding.priceInputText.text.toString().toDoubleOrNull() ?: item.price,
            brand = selectedBrand
        )

        // Simulate saving the item
        Toast.makeText(requireContext(), "Item '${updatedItem.title}' updated successfully!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
