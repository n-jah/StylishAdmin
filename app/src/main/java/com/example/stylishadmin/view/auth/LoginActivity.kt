package com.example.stylishadmin.view.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.stylishadmin.R
import com.example.stylishadmin.databinding.ActivityLoginBinding
import com.example.stylishadmin.repository.auth.AuthRepoInterface
import com.example.stylishadmin.repository.auth.AuthRepository
import com.example.stylishadmin.view.MainActivity
import com.example.stylishadmin.viewModel.auth.LoginViewModel
import com.example.stylishadmin.viewModel.auth.LoginViewModelFactory
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private  var authRepository: AuthRepoInterface = AuthRepository()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViewModels()
        // Set up observers
        observeViewModel()

        // Set up text change listeners for input validation
        setupTextWatchers()

        // Set up onClick listener for login button
        binding.login.setOnClickListener {
            val email = binding.username.text.toString().trim()
            val password = binding.password.text.toString()

            if (isInputValid(email, password)) {
                // Show progress bar when logging in
                binding.loading.visibility = View.VISIBLE
                viewModel.signIn(email, password)
            }
        }
    }

    private fun initViewModels() {

        val viewModelFactoryLogin =  LoginViewModelFactory(authRepository)
        viewModel = ViewModelProvider(this, viewModelFactoryLogin).get(LoginViewModel::class.java)
    }


    private fun isInputValid(email: String, password: String): Boolean {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.username.error = "Please enter a valid email address."
            return false
        } else {
            binding.username.error = null
        }

        if (password.isEmpty() || password.length < 6) {
            binding.password.error = "Password must be at least 6 characters long."
            return false
        } else {
            binding.password.error = null
        }

        return true
    }

    private fun setupTextWatchers() {
        binding.username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString().trim()
                if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.username.error = "Please enter a valid email address."
                } else {
                    binding.username.error = null // Clear previous error
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                val password = binding.password.text.toString().trim()
                binding.login.isEnabled = email.isNotEmpty() && password.isNotEmpty()
            }
        })

        binding.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                if (password.isNotEmpty() && password.length < 6) {
                    binding.password.error = "Password must be at least 6 characters long."
                } else {
                    binding.password.error = null // Clear previous error
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val email = binding.username.text.toString().trim()
                val password = s.toString().trim()
                binding.login.isEnabled = email.isNotEmpty() && password.isNotEmpty()

            }
        })

    }

    private fun observeViewModel() {
        // Observing loading state
        viewModel.loading.observe(this, Observer { isLoading ->
            if (isLoading) {
                binding.loading.visibility = View.VISIBLE
            } else {
                binding.loading.visibility = View.GONE
            }
        })

        // Observing login status
        viewModel.loginStatus.observe(this, Observer { isSuccess ->
            if (isSuccess) {
                // Navigate to the next screen (like an admin dashboard)
                Snackbar.make(binding.root, "Login successful!", Snackbar.LENGTH_SHORT).show()
                // Start intent for the next screen (e.g., AdminDashboardActivity)
                startActivity(Intent (this, MainActivity::class.java))
            }
        })

        // Observing error messages
        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
            }
            // Hide progress bar when there's an error
            binding.loading.visibility = View.GONE
        })
    }
}