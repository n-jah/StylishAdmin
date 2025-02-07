package com.example.stylishadmin.view

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stylishadmin.R
import com.example.stylishadmin.adapter.SizeAdapter
import com.example.stylishadmin.databinding.DialogSizeManagerBinding
import com.example.stylishadmin.model.items.Size
import com.example.stylishadmin.repository.items.ItemsRepoImp
import com.example.stylishadmin.viewModel.items.ItemsViewModel
import com.example.stylishadmin.viewModel.items.ItemsViewModelFactory

class ManageSizeDialogFragment : DialogFragment() {

    private var _binding: DialogSizeManagerBinding? = null
    private val binding get() = _binding!!
    private var sizeStockList = mutableListOf<Size>()
    private lateinit var adapter: SizeAdapter
    private var listener: ManageSizeDialogListener? = null


    companion object {
        private const val ARG_SIZES = "sizes"

        fun newInstance(sizes: List<Size>): ManageSizeDialogFragment {
            val fragment = ManageSizeDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_SIZES, ArrayList(sizes)) // Pass sizes as a Parcelable ArrayList
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this dialog
        _binding = DialogSizeManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the RecyclerView and button behavior
        setUpRecyclerView()
        setUpAddNewSizeButton()
        saveButton()
    }

    private fun saveButton() {

        binding.fabSave.setOnClickListener{
            val updatedSizes = sizeStockList.toList()
            listener?.onSizesUpdated(updatedSizes)
            dismiss()
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


    private fun setUpRecyclerView() {
        // Sample sizes to populate the RecyclerView initially

        sizeStockList = (arguments?.getParcelableArrayList<Size>(ARG_SIZES) ?: emptyList()).toMutableList()

        // Initialize the adapter with sizeStockList
        adapter = SizeAdapter(
            sizes = sizeStockList,
            onIncreaseClick = { size ->
                size.stock++
                notifySizeChanged(size)
            },
            onDecreaseClick = { size ->
                if (size.stock > 0) {
                    size.stock--
                    notifySizeChanged(size)
                } else {
                    Toast.makeText(requireContext(), "Stock cannot be negative", Toast.LENGTH_SHORT).show()
                }
            },
            onStockChange = { size ->
                if (size.stock > 0){
                    size.stock = size.stock
                    notifySizeChanged(size)
                }
            },
            onDeleteClick = { size ->
                val position = sizeStockList.indexOf(size)
                sizeStockList.remove(size)
                adapter.notifyItemRemoved(position)
            }
        )

        // Bind the RecyclerView to the adapter and layout manager
        val rv =binding.recyclerViewSizes

            rv.adapter = this@ManageSizeDialogFragment.adapter
            rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rv.setHasFixedSize(true)


    }

    private fun setUpAddNewSizeButton() {
        binding.buttonAddNew.setOnClickListener {
            val sizeName = binding.editTextNewSize.text.toString().trim()
            val stock = binding.editTextNewStock.text.toString().toIntOrNull()

            when {
                sizeName.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please enter a size name", Toast.LENGTH_SHORT).show()
                }
                stock == null || stock <= 0 -> {
                    Toast.makeText(requireContext(), "Please enter a valid stock value", Toast.LENGTH_SHORT).show()
                }
                sizeStockList.any { it.size.equals(sizeName, ignoreCase = true) } -> {
                    Toast.makeText(requireContext(), "Size already exists", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val newSize = Size(sizeName, stock)
                    sizeStockList.add(newSize)
                    adapter.notifyItemInserted(sizeStockList.size - 1)
                    binding.recyclerViewSizes.adapter = adapter

                    Log.d("ManageSizeDialogFragment", "New size added: $newSize")
                    // Clear input fields after adding
                    binding.editTextNewSize.text?.clear()
                    binding.editTextNewStock.text?.clear()
                }
            }
        }
    }
    private fun notifySizeChanged(size: Size) {
        val position = sizeStockList.indexOf(size)
        if (position != -1) {
            binding.recyclerViewSizes.post {
                adapter.notifyItemChanged(position)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        // Clear the listener reference
        listener = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onSizesUpdated(sizeStockList)
    }

    fun setManageSizeDialogListener(listener: ManageSizeDialogListener) {
        this.listener = listener
    }


}

interface ManageSizeDialogListener {
    fun onSizesUpdated(updatedSizes: List<Size>)
}

