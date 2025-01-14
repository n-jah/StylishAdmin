    package com.example.stylishadmin.view

    import android.content.Context
    import android.net.Uri
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ArrayAdapter
    import android.widget.Toast
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.ViewModelProvider
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.example.stylishadmin.adapter.EditItemImagesAdapter
    import com.example.stylishadmin.adapter.SizeStockAdapter
    import com.example.stylishadmin.databinding.FragmentEditItemBinding
    import com.example.stylishadmin.model.brands.Brand
    import com.example.stylishadmin.model.items.Item
    import com.example.stylishadmin.model.items.Size
    import com.example.stylishadmin.repository.brands.BrandsRepoImpl
    import com.example.stylishadmin.repository.items.ItemsRepoImp
    import com.example.stylishadmin.utils.ImageUtils.compressBitmap
    import com.example.stylishadmin.utils.ImageUtils.decodeUriToBitmap
    import com.example.stylishadmin.utils.ImageUtils.resizeBitmap
    import com.example.stylishadmin.utils.UriUtils.isUri
    import com.example.stylishadmin.utils.UriUtils.isUrl
    import com.example.stylishadmin.viewModel.brands.BrandsViewModel
    import com.example.stylishadmin.viewModel.brands.BrandsViewModelFactory
    import com.example.stylishadmin.viewModel.items.ItemsViewModel
    import com.example.stylishadmin.viewModel.items.ItemsViewModelFactory
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext


    class EditItemFragment : Fragment(), ManageSizeDialogListener {

        private var _binding: FragmentEditItemBinding? = null
        private val binding get() = _binding!!
        private lateinit var brandAdapter: ArrayAdapter<String>
        private var brandList: MutableList<Brand> = mutableListOf()
        private lateinit var sizesAdapter: SizeStockAdapter
        private lateinit var imagesAdapter: EditItemImagesAdapter
        private var itemsImagesUris: MutableList<String> = mutableListOf()
        private var currentItemImagesUrls: MutableList<String> = mutableListOf()
        private lateinit var itemsViewModel: ItemsViewModel
        private lateinit var brandsViewModel: BrandsViewModel
        private val deletedImageUrls = mutableListOf<String>()
        private val maxSize: Long = 2 * 1024 * 1024  // 2 MB in bytes

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentEditItemBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            setupViewModels()
            observers()
                setupUI()
        }

        private fun observers() {
            itemsViewModel.loading.observe(viewLifecycleOwner){
                setupLoading(it)
            }
            brandsViewModel.loading.observe(viewLifecycleOwner){
                setupLoading(it)
            }
        }

        private fun setupUI() {

            //get current Item form the args after navigation
            val args = EditItemFragmentArgs.fromBundle(requireArguments())
            val item = args.itemData

            setInfoToUI(item)

            // Save Button Click
            binding.saveProductButton.setOnClickListener {
                saveItem(item)
            }

            //back
            binding.backButton.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            //delete
            binding.btnDelete.setOnClickListener {
                itemsViewModel.deleteItem(item.id){
                    if (it.isSuccess){
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        }

    private fun setInfoToUI(item: Item) {
        binding.itemNameInputText.setText(item.title)
        binding.priceInputText.setText(item.price.toString())
        binding.productDescription.setText(item.description)
        currentItemImagesUrls.addAll(item.imgUrl)
        setupBrandSpinner(item.brand)
        setUpRVSizeAndImages(item)
    }

    private fun setupViewModels() {
        val itemsViewModelFactory = ItemsViewModelFactory(ItemsRepoImp())
        itemsViewModel = ViewModelProvider(requireActivity(), itemsViewModelFactory)[ItemsViewModel::class.java]
        val brandsViewModelFactory = BrandsViewModelFactory(BrandsRepoImpl())
        brandsViewModel = ViewModelProvider(requireActivity(), brandsViewModelFactory)[BrandsViewModel::class.java]
        brandsViewModel.getBrands()
    }

    private fun setUpRVSizeAndImages(item: Item) {
        setupSizes(item)
        setupImages(item)
    }

        private fun setupImages(item: Item) {
            val itemImages = item.imgUrl.toMutableList()
            imagesAdapter = EditItemImagesAdapter(
                itemImages,
                onRemoveImageClick = { image ->
                    if (isUri(image)) {
                        // Remove from itemsImagesUris
                        itemsImagesUris.remove(image)
                    } else if (isUrl(image)) {
                        // Remove from item.imgUrl
                        val index = item.imgUrl.indexOf(image)
                        deletedImageUrls.add(image)
                        currentItemImagesUrls.remove(image)
                        if (index >= 0) {
                            item.imgUrl.removeAt(index)
                            binding.itemImagesRv.adapter?.notifyItemRemoved(index)
                        }
                    }
                    // Remove from adapter
                    imagesAdapter.removeImage(image)
                }, onAddClick = {
                    pickImages()
                })
            binding.itemImagesRv.adapter = imagesAdapter
            binding.itemImagesRv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.itemImagesRv.setHasFixedSize(true)

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

        //here we collect the info and update it
        private fun saveItem(item: Item) {
            // Validate fields before saving
            if (!validateFields()) {
                return
            }
            val selectedBrand = brandList[binding.brandDropdown.selectedItemPosition].brandName
            val sizes = sizesAdapter.getSizes()
            val updatedItem = item.copy(
                title = binding.itemNameInputText.text.toString(),
                description = binding.productDescription.text.toString(),
                price = binding.priceInputText.text.toString().toDoubleOrNull() ?: item.price,
                brand = selectedBrand,
                sizes = sizes
            )

         // Show loading indicator
         setupLoading(true)

        // Delete all deleted images from storage
        deleteImagesFromStorage(deletedImageUrls)

            // Upload new images and update the item
            addUrlsToItem(updatedItem) { afterUpdatedItem ->
                setupLoading(false)

                itemsViewModel.updateItem(item.id, afterUpdatedItem){
                    if (it.isSuccess){
                        Toast.makeText(
                            requireContext(),
                            "Item '${updatedItem.title}' updated successfully!",
                            Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressedDispatcher.onBackPressed()

                    }
                }
                Log.d("NewItemFragment", "the item after updated$afterUpdatedItem")
            }

            // Simulate saving the item
            Toast.makeText(
                requireContext(),
                "Item '${updatedItem.title}' updated successfully!",
                Toast.LENGTH_SHORT
            ).show()
        }

        private fun validateFields(): Boolean {

            // Validate Item Name
            val itemName = binding.itemNameInputText.text.toString().trim()
            if (itemName.isEmpty()) {
                Toast.makeText(requireContext(), "Item name cannot be empty", Toast.LENGTH_SHORT).show()
                return false
            }

            // Validate Price
            val priceText = binding.priceInputText.text.toString().trim()
            val price = priceText.toDoubleOrNull()
            if (price == null || price <= 0) {
                Toast.makeText(requireContext(), "Price must be a valid number greater than 0", Toast.LENGTH_SHORT).show()
                return false
            }

            // Validate Brand
            val selectedBrand = binding.brandDropdown.selectedItemPosition
            if (selectedBrand == -1) {
                Toast.makeText(requireContext(), "Please select a brand", Toast.LENGTH_SHORT).show()
                return false
            }
            // Validate Sizes
            val sizes = sizesAdapter.getSizes()
            if (sizes.isEmpty()) {
                Toast.makeText(requireContext(), "At least one size must be added", Toast.LENGTH_SHORT).show()
                return false
            }
            // Validate Images
            val totalImages = currentItemImagesUrls.size + itemsImagesUris.size
            if (totalImages == 0) {
                Toast.makeText(requireContext(), "At least one image must be added", Toast.LENGTH_SHORT).show()
                return false
            }
            return true

        }

            private fun deleteImagesFromStorage(imageUrls: MutableList<String>) {

            if (imageUrls.isEmpty()) return // Skip if no URLs to delete

            CoroutineScope(Dispatchers.IO).launch {
                // Delete images from storage
                imageUrls.forEach {url->
                    itemsViewModel.deleteItemImageFromStorage(url) { result ->
                        if (result.isSuccess) {
                            Log.d("EditItemFragment", "Deleted images from storage: $imageUrls")
                            deletedImageUrls.clear() // Clear the list after deletion
                        } else {
                            Log.e("EditItemFragment", "Failed to delete images: $imageUrls")
                        }
                    }
                }
                // Clear the list after deletion
                withContext(Dispatchers.Main) {
                    deletedImageUrls.clear()
                }
            }
        }

        private fun addUrlsToItem(updatedItem: Item, callBack: (Item) -> Unit) {
            if (itemsImagesUris.isNotEmpty()){
                uploadUrisAndGetURLs(itemsImagesUris, updatedItem.id) { urls ->
                    currentItemImagesUrls.addAll(urls)
                    updatedItem.imgUrl = currentItemImagesUrls as ArrayList<String>
                    Log.d("NewItemFragment", "Urls of all after uploading${updatedItem.imgUrl}")
                    callBack(updatedItem)
                }
            }else{
                callBack(updatedItem)
            }
        }

        private fun uploadUrisAndGetURLs(
            itemsImagesUris: MutableList<String>,
            id: String,
            callBack: (MutableList<String>) -> Unit
        ) {
            val compressImages: MutableList<ByteArray> = mutableListOf()
            //check if image bigger than 2 MB
                // Show loading indicator
        setupLoading(true)

            CoroutineScope(Dispatchers.IO).launch {
                itemsImagesUris.forEach { uri ->
                    //compretion
                    compressImages.add(
                        compressImagesWithLimit(
                            Uri.parse(uri),
                            requireContext(),
                            1000,
                            1000)
                    )
                }
                withContext(Dispatchers.Main) {
                    itemsViewModel.uploadImagesBackWithUrls(compressImages.toList(), id) { urls ->
                        if (urls.isSuccess) {
                            urls.getOrNull()?.let {
                                callBack(it.toMutableList())
                                Log.d("NewItemFragment", "Item with URLs: $it")
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to upload images",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

        }

        fun compressImagesWithLimit(
            imageUri: Uri,
            context: Context,
            maxWidth: Int,
            maxHeight: Int
        ): ByteArray {
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

        private fun setupSizes(item: Item) {
            //get sizes to mutable list of pair <string , int>
            val sizes = item.sizes.toMutableList()
            sizesAdapter = SizeStockAdapter(sizes, onEditClick = {
                val dialog = ManageSizeDialogFragment.newInstance(sizes)
                dialog.setManageSizeDialogListener(this)
                dialog.show(childFragmentManager, "ManageSizeDialogFragment")
            }
            )
            binding.sizeStockRv.adapter = sizesAdapter
            binding.sizeStockRv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.sizeStockRv.setHasFixedSize(true)
        }

        private fun setupBrandSpinner(selectedBrand: String) {
            // Create Adapter for Spinner
            brandAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
            brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.brandDropdown.adapter = brandAdapter

            // Simulate loading brand data
            loadBrands(selectedBrand)
        }

        private fun loadBrands(selectedBrand: String) {
            val brands = brandsViewModel.brands.value
            if (brands != null) {
                brandList.addAll(brands.filter { it.brandName.lowercase() != "all" })
                // Update Spinner Adapter
                brandAdapter.clear()
                brandAdapter.addAll(brandList.map { it.brandName })
                // Set selected brand
                val selectedIndex = brandList.indexOfFirst { it.brandName == selectedBrand }
                if (selectedIndex >= 0) {
                    binding.brandDropdown.setSelection(selectedIndex)
                }

            }
        }


        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        override fun onSizesUpdated(updatedSizes: List<Size>) {
            sizesAdapter.updateSizes(updatedSizes)
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
