package com.example.myburrito

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.myburrito.database.BurritoDbHelper
import com.google.android.material.textfield.TextInputEditText

class SignupActivity : AppCompatActivity() {
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var signupButton: Button
    private lateinit var navigateBackButton: ImageView
    private lateinit var dbHelper : BurritoDbHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        usernameEditText = findViewById(R.id.InputInput_username)
        emailEditText = findViewById(R.id.TextInput_email)
        passwordEditText = findViewById(R.id.TextInput_password)
        signupButton = findViewById(R.id.button_signup)
        navigateBackButton = findViewById(R.id.button_navigate_back)
        dbHelper = BurritoDbHelper(this)

        signupButton.setOnClickListener {
            performSignup()
        }

        navigateBackButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun performSignup() {
        // Retrieve user input
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        val userId = dbHelper.insertUser(username, email, password)

        if (userId != -1L) {
            navigateToMainScreen(username)
        } else {
            Toast.makeText(this, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show()
        }

        navigateToMainScreen(username)
    }

    private fun navigateToMainScreen(username: String) {
        val intent = Intent(this, MainActivity::class.java)
        saveUsernameToSharedPreferences(username);
        startActivity(intent)

        finish()
    }

    private fun saveUsernameToSharedPreferences(username: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()
    }
}