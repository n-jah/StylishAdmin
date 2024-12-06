    package com.example.stylishadmin.view

    import android.os.Bundle
    import android.util.Log
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import androidx.lifecycle.ViewModelProvider
    import androidx.lifecycle.lifecycleScope
    import com.example.stylishadmin.R
    import com.example.stylishadmin.databinding.ActivityMainBinding
    import com.example.stylishadmin.model.user.User
    import com.example.stylishadmin.repository.users.UserRepoImp
    import com.example.stylishadmin.repository.users.UserRepoInterface
    import com.example.stylishadmin.viewModel.users.UserViewModelFactory
    import com.example.stylishadmin.viewModel.users.UsersViewModel
    import com.google.firebase.Firebase
    import com.google.firebase.auth.FirebaseAuth
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.runBlocking
    import kotlinx.coroutines.tasks.await

    class MainActivity : AppCompatActivity() {
        private lateinit var binding: ActivityMainBinding
        private lateinit var viewModel: UsersViewModel
        private  var userRepoInterface = UserRepoImp()


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

            val viewModelFacotry = UserViewModelFactory(userRepoInterface)
            viewModel = ViewModelProvider(this, viewModelFacotry)[UsersViewModel::class.java]




        }
    }