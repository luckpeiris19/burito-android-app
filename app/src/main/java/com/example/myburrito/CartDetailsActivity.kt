package com.example.myburrito

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myburrito.adapter.CartItemAdapter
import com.example.myburrito.database.BurritoDbHelper
import java.util.ArrayList

class CartDetailsActivity : AppCompatActivity() {
    lateinit var returnBack: ImageView
    lateinit var totalPrice_tv: TextView
    lateinit var recyclerView: RecyclerView
    lateinit var checkoutButton: Button
    lateinit var adapter: CartItemAdapter
    lateinit var db: BurritoDbHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_details)
        initViews()
        db = BurritoDbHelper(this)
        calculateTotalPrice()

        val selectedBurritos: List<Burrito>? = db.getAllCartBurritos()

        if (selectedBurritos != null) {
            adapter = CartItemAdapter(
                this,
                selectedBurritos!!,
                this::onAddQuantityClickListener,
                this::onMinusQuantityClickListener
            )
            recyclerView.adapter = adapter
        }
        returnBack.setOnClickListener {
            onBackPressed()
        }

        checkoutButton.setOnClickListener {
            startActivity(Intent(this, CheckoutActivity::class.java))
            finish()
        }


    }

    fun onAddQuantityClickListener(burrito: Burrito) {
        db.updateBurritoQuantity(burrito, burrito.quantity + 1)
        calculateTotalPrice()
    }

    fun onMinusQuantityClickListener(burrito: Burrito) {
        if (burrito.quantity - 1 == 0) {
            db.deleteBurrito(burrito)
            adapter.cartItems = db.getAllCartBurritos()
            adapter.notifyDataSetChanged()

        } else {
            db.updateBurritoQuantity(burrito, burrito.quantity - 1)
        }
        calculateTotalPrice()
    }

    private fun calculateTotalPrice() {
        val cartBurritos = db.getAllCartBurritos()
        val totalPrice = cartBurritos.sumByDouble { it.price * it.quantity }
        totalPrice_tv.text = String.format("%.2f$", totalPrice)
    }

    fun initViews() {
        returnBack = findViewById(R.id.iv_back)
        totalPrice_tv = findViewById(R.id.tv_total_price)
        recyclerView = findViewById(R.id.rv_cart)
        checkoutButton = findViewById(R.id.button_checkout)
//        navbar = findViewById(R.id.bottom_navbar)
    }
}