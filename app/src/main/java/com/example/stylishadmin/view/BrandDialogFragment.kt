package com.example.stylishadmin.view

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stylishadmin.R
import com.example.stylishadmin.adapter.BrandsAdapter
import com.example.stylishadmin.adapter.DialogBrandsAdapter
import com.example.stylishadmin.databinding.FragmentBrandDialogBinding
import com.example.stylishadmin.model.brands.Brand
import com.example.stylishadmin.repository.brands.BrandsRepoImpl
import com.example.stylishadmin.viewModel.brands.BrandsViewModel
import com.example.stylishadmin.viewModel.brands.BrandsViewModelFactory

class BrandDialogFragment : DialogFragment() {

    private var  _binding : FragmentBrandDialogBinding ? = null
    private val binding get() = _binding!!

    private lateinit var brandsViewModel : BrandsViewModel

    private var brandsList = mutableListOf<Brand>()

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
    ): View? {
        // Inflate the layout for this fragment
         _binding = FragmentBrandDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intiViewModel()
        setUpRecyclerView()
        setupAddNewSize()
        setupAddImage()
        setupSave()
    }

    private fun intiViewModel() {
        val brandsFactory = BrandsViewModelFactory(BrandsRepoImpl())
        brandsViewModel = ViewModelProvider(requireActivity(),brandsFactory)[BrandsViewModel::class.java]

    }

    private fun setupAddImage() {
        binding.addImageBrand.setOnClickListener {

            brandsViewModel.addImage()
        }
        
    }

    private fun setupSave() {
        binding.fabSave.setOnClickListener {
            if (brandsList != adapter.brands){

                Toast.makeText(requireContext(), "save", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "no thing to save", Toast.LENGTH_SHORT).show()
                binding.editTextNewBrand.error 

            }
        }
    }

    private fun setupAddNewSize() {
        binding.buttonAddNew.setOnClickListener {

            if (binding.editTextNewBrand.text?.isNotEmpty()!!){
                brandsList.add(Brand(
                    binding.editTextNewBrand.text.toString(), "https://firebasestorage.googleapis.com/v0/b/stylish-dc0d1.appspot.com" +
                            "/o/2024-08-24%2006-22-09.png?alt=media&token=5e2e3e50-bac4-4192-8ddc-34865d35d9a5"))
                //save image link to firebase
                // @TODO
                adapter.update(brandsList.reversed())
                binding.editTextNewBrand.setText("")

            }else{
                Toast.makeText(requireContext(), "Empty ", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setUpRecyclerView() {

        brandsList =
            arguments?.getParcelableArrayList<Brand>(ARG_BRANDS)?.filter { it.brandName != "All" }!!
                .toMutableList()
        adapter = DialogBrandsAdapter(brandsList)
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
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT  // Wrap content for height
        )
        dialog?.window?.setGravity(Gravity.CENTER) // Ensure it shows centered


        // Set the background drawable for the dialog

        // background
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_background)


    }
}
