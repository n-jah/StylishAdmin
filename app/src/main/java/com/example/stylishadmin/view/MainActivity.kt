    package com.example.stylishadmin.view

    import android.content.Intent
    import android.os.Bundle
    import android.util.Log
    import android.view.View
    import android.widget.Toast
    import androidx.activity.enableEdgeToEdge
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import androidx.lifecycle.ViewModelProvider
    import androidx.lifecycle.lifecycleScope
    import com.example.stylishadmin.R
    import com.example.stylishadmin.databinding.ActivityMainBinding
    import com.example.stylishadmin.model.items.BRANDS
    import com.example.stylishadmin.model.items.Item
    import com.example.stylishadmin.model.items.Size
    import com.example.stylishadmin.model.user.User
    import com.example.stylishadmin.repository.items.ItemsRepoImp
    import com.example.stylishadmin.repository.items.ItemsRepositoryInterface
    import com.example.stylishadmin.repository.orders.OrdersRepoImp
    import com.example.stylishadmin.repository.orders.OrdersRepositoryInterface
    import com.example.stylishadmin.repository.users.UserRepoImp
    import com.example.stylishadmin.repository.users.UserRepoInterface
    import com.example.stylishadmin.viewModel.items.ItemsViewModel
    import com.example.stylishadmin.viewModel.items.ItemsViewModelFactory
    import com.example.stylishadmin.viewModel.orders.OrdersViewModel
    import com.example.stylishadmin.viewModel.orders.OrdersViewModelFactory
    import com.example.stylishadmin.viewModel.users.UserViewModelFactory
    import com.example.stylishadmin.viewModel.users.UsersViewModel
    import com.google.firebase.Firebase
    import com.google.firebase.auth.FirebaseAuth
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.runBlocking
    import kotlinx.coroutines.tasks.await

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

            val newItem = Item("newItem",999.9, arrayListOf("https://th.bing.com/th/id/OIP.qWDIzrk3j2HDpBNP7kBa9wHaEK?rs=1&pid=ImgDetMain"),
                listOf(Size("ahmed",9),Size("mohamed",9)),"this my new item I added","",9.9,
                BRANDS.PUMA.toString().lowercase()
            )

            binding.button.setOnClickListener {
//             viewModel.addItemsToOnlineDB(newItem){
//                 message ->
//                 Toast.makeText(this,message.toString(), Toast.LENGTH_LONG).show()
//             }


                getImagesofItems(2)
        //        pickImageFromGallery()
                viewModel.getItems()

            }

            //get orders
            viewModel.items.observe(this){
                binding.tv.text = it?.first().toString()
            }


            viewModel.loading.observe(this){
                binding.loadingbar.visibility = if (it) View.VISIBLE else View.GONE
            }





        }

        private fun getImagesofItems(itemId: String) {
            viewModel.getImagesUrlsOfItem(itemId){
                result ->


            }
        }

        private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            if (uris.isNotEmpty()) {
                val selectedUris = uris.map { it.toString() }
                // Now you can upload all the selected images
                viewModel.uploadImagesBackWithUrls(selectedUris, "1") { result ->
                    if (result.isSuccess) {
                        val uploadedImageUrls = result.getOrNull()

                        // Handle the uploaded image URLs as needed
                        Toast.makeText(this, "Images uploaded successfully", Toast.LENGTH_LONG).show()
                    } else {
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
