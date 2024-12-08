    package com.example.stylishadmin.view

    import android.os.Bundle
    import android.view.View
    import android.widget.Toast
    import androidx.activity.enableEdgeToEdge
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import androidx.lifecycle.ViewModelProvider
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.example.stylishadmin.R
    import com.example.stylishadmin.adapter.testImageAdapter
    import com.example.stylishadmin.databinding.ActivityMainBinding
    import com.example.stylishadmin.repository.items.ItemsRepoImp
    import com.example.stylishadmin.repository.items.ItemsRepositoryInterface
    import com.example.stylishadmin.viewModel.items.ItemsViewModel
    import com.example.stylishadmin.viewModel.items.ItemsViewModelFactory

    class MainActivity : AppCompatActivity() {
        private lateinit var binding: ActivityMainBinding
        private lateinit var viewModel: ItemsViewModel
        private  var itemsRepository: ItemsRepositoryInterface = ItemsRepoImp()


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            val viewModelFacotry = ItemsViewModelFactory(itemsRepository)
            viewModel = ViewModelProvider(this, viewModelFacotry)[ItemsViewModel::class.java]



            setUpRv()

            binding.button.setOnClickListener {
                pickImageFromGallery()

            }

            viewModel.loading.observe(this){
                binding.loadingbar.visibility = if (it) View.VISIBLE else View.GONE
            }





        }

        private fun setUpRv() {
            binding.rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.rv.adapter = testImageAdapter(emptyList())
            binding.rv.setHasFixedSize(true)

        }

        private fun getImagesofItems(itemId: String) {
            viewModel.getItem(itemId)
            viewModel.item.observe(this){
                if (it != null){
                    binding.rv.adapter = testImageAdapter(it.imgUrl)
                }
            }
        }

        private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            if (uris.isNotEmpty()) {
                val selectedUris = uris.map { it.toString() }
                viewModel.uploadImagesAndPutUrlsInRemoteStorage(selectedUris,"2"){
                    result ->
                    if (result.isSuccess){
                        getImagesofItems("2")
                        Toast.makeText(this, "Images uploaded successfully", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(this, "Failed to upload images: ${result.exceptionOrNull()}", Toast.LENGTH_LONG).show()
                    }

                }
            } else {
                Toast.makeText(this, "Image selection cancelled or no images selected", Toast.LENGTH_SHORT).show()
            }
        }

        private fun pickImageFromGallery() {
            pickImageLauncher.launch(arrayOf("image/*")) // Launches the picker for multiple image selection
        }

    }
