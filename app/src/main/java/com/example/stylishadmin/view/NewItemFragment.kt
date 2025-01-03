package com.example.stylishadmin.view

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stylishadmin.adapter.ImagesAdapter
import com.example.stylishadmin.adapter.SizeStockAdapter
import com.example.stylishadmin.databinding.FragmentNewItemBinding
import com.example.stylishadmin.model.brands.Brand
import com.example.stylishadmin.model.items.Item
import com.example.stylishadmin.model.items.Size
import com.example.stylishadmin.repository.brands.BrandsRepoImpl
import com.example.stylishadmin.repository.items.ItemsRepoImp
import com.example.stylishadmin.utils.ImageUtils.compressBitmap
import com.example.stylishadmin.utils.ImageUtils.decodeUriToBitmap
import com.example.stylishadmin.utils.ImageUtils.resizeBitmap
import com.example.stylishadmin.utils.ImageUtils.shouldCompressImage
import com.example.stylishadmin.viewModel.brands.BrandsViewModel
import com.example.stylishadmin.viewModel.brands.BrandsViewModelFactory
import com.example.stylishadmin.viewModel.items.ItemsViewModel
import com.example.stylishadmin.viewModel.items.ItemsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    private lateinit var itemsViewModel : ItemsViewModel

    val maxSize : Long = 2 * 1024 * 1024  // 2 MB in bytes



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
        observers()
        setUpUI()
    }

    private fun observers() {
        brandsViewModel.getBrands()
        brandsViewModel.brands.observe(viewLifecycleOwner) { brandResponse ->
            if (brandResponse != null) {
                brandList.clear()
                val brands = brandResponse.filter { it.brandName.lowercase() !=  "all"}
                brandList.addAll(brands)
                // Update spinner adapter data
                brandAdapter.clear()
                val brandNames = brandList.map { it.brandName }
                brandAdapter.addAll(brandNames)
                binding.brandDropdownNew.setSelection(0)
            }
        }
        itemsViewModel.loading.observe(viewLifecycleOwner){
            setupLoading(it)
        }
    }

    private fun initViewModels() {
        val brandsFactory = BrandsViewModelFactory(BrandsRepoImpl())
        brandsViewModel = ViewModelProvider(requireActivity(), brandsFactory)[BrandsViewModel::class.java]

        val itemsFactory = ItemsViewModelFactory(ItemsRepoImp())
        itemsViewModel = ViewModelProvider(requireActivity(), itemsFactory)[ItemsViewModel::class.java]


    }

    private fun setUpUI() {
        // get Back with
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            //when  iam here should start the   fragment


        }

        setUpRecyclerview()
        setUpSpinner()

        //save item
        binding.saveProductButton.setOnClickListener {
            saveItem()
        }

    }

    private fun saveItem() {
        // Collect data from UI
        val productName = binding.productNameInput.text.toString().trim()
        val productDescription =  binding.productDescription.text.toString().trim()
        val price = binding.priceInputText.text.toString().trim().toDoubleOrNull() ?: 0.0
        val selectedBrandPosition = binding.brandDropdownNew.selectedItemPosition
        val selectedBrand = if (selectedBrandPosition != AdapterView.INVALID_POSITION) {
            brandList[selectedBrandPosition]
        } else null
        val itemSizes = sizesAdapter.getSizes() // Get sizes from the adapter
        val itemImages = itemsImagesUris // Get image URIs

        // Validate data
        if (productName.isEmpty()) {
            Toast.makeText(requireContext(), "Item name is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (price <= 0.0) {
            Toast.makeText(requireContext(), "Price is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (productDescription.isEmpty()){
            Toast.makeText(requireContext(), "Item name is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedBrand == null) {
            Toast.makeText(requireContext(), "Please select a brand", Toast.LENGTH_SHORT).show()
            return
        }
        if (itemSizes.isEmpty()) {
            Toast.makeText(requireContext(), "Add at least one size and stock", Toast.LENGTH_SHORT).show()
            return
        }
        if (itemImages.isEmpty()) {
            Toast.makeText(requireContext(), "Add at least one image", Toast.LENGTH_SHORT).show()
            return
        }
        // Prepare data for upload


        val newItem = Item(
            title = productName,
            description = productDescription,
            price =  price,
            brand = selectedBrand.brandName,
            sizes = itemSizes,
            imgUrl = itemImages.toCollection(ArrayList())
        )
        // Log the prepared data

        // first thing is adding the item to the firbase and get the id
        //second thing  is use id to save compressd images to online storage and get URLs
        //third thing is update the item with the URls of compresd images that in storage

        itemsViewModel.addItemsToOnlineDB(newItem){
            if (it.isSuccess){
                Log.d("NewItemFragment","${it.getOrNull()}")

                addUrlsToItem(newItem, it.getOrNull().toString()) { updatedItem->
                    Log.d("NewItemFragment","the updated item${updatedItem}")
                        itemsViewModel.updateItem(updatedItem.id, updatedItem) { reslut->
                            if (reslut.isSuccess){
                                Toast.makeText(requireContext(),reslut.getOrNull().toString(),Toast.LENGTH_SHORT).show()
                                //CLOSE THE FRAGMENT
                                requireActivity().onBackPressedDispatcher.onBackPressed()

                            }
                        }
                    Log.d("NewItemFragment", "updated Item: $updatedItem")
                }
                Toast.makeText(requireContext(), "Item added successfully the id : ${it.getOrNull().toString()}", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "Failed to add item", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun addUrlsToItem(item: Item ,id: String, callBack: (Item)->Unit ) {
        uploadUrisAndGetURLs(itemsImagesUris, id) { urls ->
            if (urls != emptyList<String>()) {
                item.imgUrl.clear()
                item.id = id
                item.imgUrl.addAll(urls)
                callBack(item)
            }else {
                callBack(item)
            }
        }
    }



    private fun uploadUrisAndGetURLs(itemsImagesUris: MutableList<String>, itemId: String , callBack : (MutableList<String>)->Unit ) {

        val compressImages : MutableList<ByteArray> = mutableListOf()
        //check if image bigger than 2 MB
        CoroutineScope(Dispatchers.IO).launch {
            itemsImagesUris.forEach{uri->
                //compretion
                compressImages.add(compressImagesWithLimit(Uri.parse(uri), requireContext(), 1000, 1000, maxSize))
            }
            withContext(Dispatchers.Main){
                itemsViewModel.uploadImagesBackWithUrls(compressImages.toList(), itemId){ urls ->
                    if (urls.isSuccess){
                        urls.getOrNull()?.let {
                            callBack(it.toMutableList())
                            Log.d("NewItemFragment", "Item with URLs: $it")
                        }
                    }else{
                        Toast.makeText(requireContext(), "Failed to upload images", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun setUpSpinner() {
        // Create the adapter with an empty list
        brandAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.brandDropdownNew.adapter = brandAdapter
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
    fun compressImagesWithLimit(imageUri: Uri, context: Context, maxWidth: Int, maxHeight: Int, maxSize: Long): ByteArray {
        return try {
            val bitmap = decodeUriToBitmap(context, imageUri)
            if (bitmap != null) {
                val resizedBitmap = resizeBitmap(bitmap, maxWidth, maxHeight)
                compressBitmap(resizedBitmap)
            } else {
                Log.e("ImageResize", "Failed to decode the URI into a Bitmap")
                ByteArray(0)
            }
        } catch (e: Exception) {
            Log.e("ImageResize", "Error compressing image", e)
            ByteArray(0)
        }
    }

    fun setupLoading(isLoading: Boolean){
        if (isLoading){
            binding.contentLoadingProgressBar.visibility = View.VISIBLE
            binding.contentLoadingProgressBar.isIndeterminate = true
            binding.contentLoadingProgressBar.progress = 0
            binding.contentLoadingProgressBar.max = 100
            binding.contentLoadingProgressBar.show()

        }else {
            binding.contentLoadingProgressBar.visibility = View.GONE
        }
    }



}

