package com.example.myburrito

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.myburrito.database.BurritoDbHelper


class CheckoutActivity : AppCompatActivity() {

    lateinit var moreBurrito: Button
    lateinit var backButton: ImageView
    lateinit var db : BurritoDbHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        moreBurrito = findViewById(R.id.button_more_burritos)
        backButton = findViewById(R.id.button_back_to_order)
        db = BurritoDbHelper(this)

        moreBurrito = findViewById(R.id.button_more_burritos)
        moreBurrito.setOnClickListener {
            db.removeAllBurritosFromCart()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        backButton.setOnClickListener {
            onBackPressed()
        }

    }
}