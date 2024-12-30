package com.example.stylishadmin.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stylishadmin.adapter.ImagesAdapter
import com.example.stylishadmin.adapter.SizeStockAdapter
import com.example.stylishadmin.databinding.FragmentNewItemBinding
import com.example.stylishadmin.model.brands.Brand
import com.example.stylishadmin.model.items.Size
import com.example.stylishadmin.repository.brands.BrandsRepoImpl
import com.example.stylishadmin.viewModel.brands.BrandsViewModel
import com.example.stylishadmin.viewModel.brands.BrandsViewModelFactory

class NewItemFragment : Fragment(),ManageSizeDialogListener {
    private var _binding: FragmentNewItemBinding? = null
    private val binding get() = _binding!!
    private lateinit var imagesAdapter : ImagesAdapter
    private lateinit var sizesAdapter: SizeStockAdapter
    private lateinit var brandAdapter: ArrayAdapter<String>
    private var brandList: MutableList<Brand> = mutableListOf()
    private var itemsImagesUris: MutableList<String> = mutableListOf()
    private  var sizes : MutableList<Size> =mutableListOf()
    private lateinit var brandsViewModel : BrandsViewModel



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

        initViewModels()
        setUpUI()
        observers()
    }

    private fun observers() {
        

    }

    private fun initViewModels() {
        val brandsFactory = BrandsViewModelFactory(BrandsRepoImpl())
        brandsViewModel = ViewModelProvider(requireActivity(), brandsFactory)[BrandsViewModel::class.java]
    }

    private fun setUpUI() {
        // get Back with
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            //when  iam here should start the   fragment


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


        sizesAdapter = SizeStockAdapter(sizes,
         onEditClick = {
             Toast.makeText(requireContext(),"addNewSize", Toast.LENGTH_SHORT ).show()
             val dialog = ManageSizeDialogFragment.newInstance(sizes)
             dialog.setManageSizeDialogListener(this) // Set the listener

             dialog.show(childFragmentManager, "ManageSizeDialogFragment")

            }
        )

        binding.sizeStockRv.adapter = sizesAdapter
        binding.sizeStockRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun imagesRV() {
        val images = mutableListOf<String>()
        imagesAdapter = ImagesAdapter(images, onAddClick = {
            pickImages()
        }, onRemoveImageClick = {
            imagesAdapter.removeImage(it)
            itemsImagesUris.remove(it)
        })

        binding.itemImagesRv.adapter = imagesAdapter
        binding.itemImagesRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

    }

    private fun pickImages() {
        pickMultiImageLauncher.launch(arrayOf("image/*"))
    }


    private val pickMultiImageLauncher =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            if (uris.isNotEmpty()) {
                uris.forEach { uri ->
                    uri.let {
                        itemsImagesUris.add(it.toString())
                        imagesAdapter.addImage(it.toString())
                    }
                }
            } else {
                Toast.makeText(requireContext(), "No images selected", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onSizesUpdated(updatedSizes: List<Size>) {
        sizesAdapter.updateSizes(updatedSizes)
    }


}

