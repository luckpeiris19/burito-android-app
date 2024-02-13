package com.example.myburrito

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class SplashActivity : AppCompatActivity() {
    private lateinit var getStartedButton : Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("username", null)
        if (savedUsername != null) {
            startActivity( Intent(this, MainActivity::class.java));
            finish()
        }
        getStartedButton = findViewById(R.id.get_started_button);
        getStartedButton.setOnClickListener {
            startActivity( Intent(this, LoginActivity::class.java));
        }
    }
}