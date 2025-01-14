package com.example.stylishadmin.view

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stylishadmin.R
import com.example.stylishadmin.adapter.DialogBrandsAdapter
import com.example.stylishadmin.databinding.FragmentBrandDialogBinding
import com.example.stylishadmin.model.brands.Brand
import com.example.stylishadmin.repository.brands.BrandsRepoImpl
import com.example.stylishadmin.utils.ImageUtils.getResizedAndCompressedBitmap
import com.example.stylishadmin.viewModel.brands.BrandsViewModel
import com.example.stylishadmin.viewModel.brands.BrandsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BrandDialogFragment : DialogFragment() {

    private var  _binding : FragmentBrandDialogBinding ? = null
    private val binding get() = _binding!!

    private lateinit var brandsViewModel : BrandsViewModel

    private var brandsList = mutableListOf<Brand>()

    private var imageUri : String? = null

    private lateinit var adapter: DialogBrandsAdapter

    companion object {
        private const val ARG_BRANDS = "brands"

        fun newInstance(brands: List<Brand>): BrandDialogFragment {
            val fragment = BrandDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_BRANDS, ArrayList(brands)) // Pass sizes as a Parcelable ArrayList
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
         _binding = FragmentBrandDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intiViewModel()
        intiObserver()
        setUpRecyclerView()
        setupAddNewBrand()
        setupAddImage()
    }

    private fun intiObserver() {
        brandsViewModel.loading.observe(viewLifecycleOwner) {
            setupProgressBar(it)
        }
        brandsViewModel.brands.observe(viewLifecycleOwner) {
            if (it != null) {
                val filterdList = it.filter { it.brandName != "All" }
                adapter.update(filterdList.reversed())
            }
        }

    }

    private fun intiViewModel() {
        val brandsFactory = BrandsViewModelFactory(BrandsRepoImpl())
        brandsViewModel = ViewModelProvider(requireActivity(),brandsFactory)[BrandsViewModel::class.java]

    }





    private fun setupAddNewBrand() {
        binding.buttonAddNew.setOnClickListener {
            addNameAndUrlToBrandAndUPloadit()
            adapter.update(brandsList.reversed())
            binding.editTextNewBrand.setText("")
            binding.imageofTheBrandFrame.visibility = View.GONE
        }
    }

    private fun addNameAndUrlToBrandAndUPloadit() {
        if (binding.editTextNewBrand.text.isNullOrEmpty() || imageUri == null) {
            Toast.makeText(requireContext(), "Name and image are required.", Toast.LENGTH_SHORT).show()
            return
        }
        // Launch coroutine to handle the upload
        lifecycleScope.launch {
            setupProgressBar(true)
            val brandName = binding.editTextNewBrand.text.toString()
            uploadImageGetUrl(Uri.parse(imageUri!!), brandName){url->
                if (url.isNotEmpty()) {
                    val brand = Brand(
                        brandName = brandName,
                        imgIcon = url
                    )
                    brandsViewModel.addBrand(brand)
                    brandsList.add(brand)
                    adapter.update(brandsList.reversed())
                    binding.imageofTheBrandFrame.visibility = View.GONE
                    setupProgressBar(false)
                } else {
                    setupProgressBar(false)
                }

            }

        }
    }


    private fun setUpRecyclerView() {

        brandsList =
            arguments?.getParcelableArrayList<Brand>(ARG_BRANDS)?.filter { it.brandName != "All" }!!
                .toMutableList()
        adapter = DialogBrandsAdapter(brandsList){
            brandsViewModel.deleteBrand(it)
            brandsList.remove(it)
            adapter.update(brandsList.reversed())
        }
        binding.recyclerViewBrands.apply {
            adapter = this@BrandDialogFragment.adapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        }
    }


    override fun onResume()     {
        super.onResume()
        // Explicitly set the size of the dialog to avoid it being minimized
        dialog?.window?.setLayout(
            //width 90 %
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT  // Wrap content for height
        )
        dialog?.window?.setGravity(Gravity.CENTER) // Ensure it shows centered


        // Set the background drawable for the dialog

        // background
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_background)


    }
    private val pickSingleImageLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                imageUri = uri.toString() // Save the single image URI
                getResizedAndCompressedBitmapAsync(uri, 800, 600, 80) { compressedImage ->
                    if (compressedImage != null) {
                        // Use the compressed image here
                        binding.imageofTheBrand.setImageBitmap(BitmapFactory.decodeByteArray(compressedImage, 0, compressedImage.size))
                    } else {
                        Toast.makeText(requireContext(), "Failed to process the image.", Toast.LENGTH_SHORT).show()
                    }
                }

                binding.imageofTheBrandFrame.visibility = View.VISIBLE

                Toast.makeText(requireActivity(), "Image selected successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(), "Image selection canceled.", Toast.LENGTH_SHORT).show()
            }
        }


    private fun setupAddImage() {
        binding.addImageBrand.setOnClickListener {
            pickSingleImageLauncher.launch(arrayOf("image/*"))

        }
    }
    fun setupProgressBar(isVisible : Boolean){
        if (isVisible){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }

    private  fun uploadImageGetUrl(uri: Uri, brandName: String ,states: (String)-> Unit) {
        val compressedImage = getResizedAndCompressedBitmap(requireContext(),uri, 400, 400, 50)
        if (compressedImage != null) {
            brandsViewModel.uploadCompressedImageUriAndGetURL(
                compressedImage,
                brandName

            ) { backUrl ->
                states(backUrl)
                Log.d("BrandDialogFragment", "Image uploaded successfully.$backUrl")

            }

        }
    }


    fun getResizedAndCompressedBitmapAsync(
        uri: Uri,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int,
        onComplete: (ByteArray?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = getResizedAndCompressedBitmap(requireContext(),uri, maxWidth, maxHeight, quality)
            withContext(Dispatchers.Main) {
                onComplete(result) // Call the callback on the UI thread
                if (result == null) {
                    // Use runOnUiThread to ensure Toast runs on the main thread
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Failed to process the image.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



}
