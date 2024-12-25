package com.example.stylishadmin.view

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stylishadmin.R
import com.example.stylishadmin.adapter.ImagesAdapter
import com.example.stylishadmin.adapter.SizeStockAdapter
import com.example.stylishadmin.databinding.FragmentNewItemBinding
import com.example.stylishadmin.model.brands.Brand

class NewItemFragment : Fragment() {
    private var _binding: FragmentNewItemBinding? = null
    private val binding get() = _binding!!
    private lateinit var imagesAdapter : ImagesAdapter
    private lateinit var sizesAdapter: SizeStockAdapter
    private lateinit var brandAdapter: ArrayAdapter<String>
    private var brandList: MutableList<Brand> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding  = FragmentNewItemBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpUI()
    }

    private fun setUpUI() {
        // get Back with
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            //when  iam here should start the

        }

        setUpRecyclerview()
        setUpSpinner()

    }

    private fun setUpSpinner() {

        // Create Adapter for Spinner
        brandAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.brandDropdownNew.adapter = brandAdapter

        // Simulate loading brand data
        loadBrands()
    }

    private fun loadBrands() {

        // @TODO  get the all brands from fire base
        brandList.apply {
            add(Brand("Adidas", "https://example.com/adidas-logo.png"))
            add(Brand("Nike", "https://example.com/nike-logo.png"))
            add(Brand("Puma", "https://example.com/puma-logo.png"))
        }

        brandAdapter.clear()
        brandAdapter.addAll(brandList.map { it.brandName })
        binding.brandDropdownNew.setSelection(0)

    }

    private fun setUpRecyclerview() {
        imagesRV()
        sizesRV()
    }

    private fun sizesRV() {
        val sizes = mutableListOf<Pair<String, Int>>()
        sizes.add(Pair("S", 10))
        sizes.add(Pair("M", 20))
        sizes.add(Pair("L", 15))

        sizesAdapter = SizeStockAdapter(sizes,
         onEditClick = {
             Toast.makeText(requireContext(),"addNewSize", Toast.LENGTH_SHORT ).show()
             // open the dialog
             //should open the fragment dialog form here as pop up


             val dialog = ManageSizeDialogFragment()
             dialog.show(childFragmentManager, "ManageSizeDialogFragment")

            }
        )
        binding.sizeStockRv.adapter = sizesAdapter
        binding.sizeStockRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun imagesRV() {
        val images = mutableListOf("https://th.bing.com/th/id/OIP.gGIEonJPIrZl3EpgME3JbAHaFs?rs=1&pid=ImgDetMain") // Initial list of image URLs
        imagesAdapter = ImagesAdapter(images, onAddClick = {

        }, onRemoveImageClick  = {

        })

        binding.itemImagesRv.adapter = imagesAdapter
        binding.itemImagesRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

    }








}