package com.example.stylishadmin.view
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.stylishadmin.R
import com.example.stylishadmin.databinding.ActivityMainBinding
import com.example.stylishadmin.model.orders.Order

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        binding.bottomNavigationView.post {
            val navController = findNavController(R.id.nav_host_fragment)
            binding.bottomNavigationView.setupWithNavController(navController)
        }

    }

}
