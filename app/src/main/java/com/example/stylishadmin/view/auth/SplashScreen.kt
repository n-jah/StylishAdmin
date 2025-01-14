package com.example.stylishadmin.view.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.stylishadmin.R
import com.example.stylishadmin.view.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_splash)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Delay for a few seconds before starting the next activity
        Handler().postDelayed({
            // Check if the user is signed in
            if (isUserSignedIn()) {
                // If signed in, navigate to the main activity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // If not signed in, navigate to the sign-in or sign-up activity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 1000)
    }

    private fun isUserSignedIn(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            Log.d("SplashScreen", "User is signed in: $currentUser")
        } else {
            Log.d("SplashScreen", "No user is signed in.")
        }
        return currentUser != null

    }
}