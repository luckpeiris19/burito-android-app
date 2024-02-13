package com.example.myburrito

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myburrito.adapter.BurritoAdapter
import com.example.myburrito.database.BurritoDbHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class FavoriteBurritosActivity : AppCompatActivity() {
    lateinit var adapter: BurritoAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var db: BurritoDbHelper
    lateinit var BottomNavbar: BottomNavigationView

    lateinit var burritoList : List<Burrito>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_burritos)
        recyclerView = findViewById(R.id.rv_favorite_burritos)
        BottomNavbar = findViewById(R.id.bottom_navbar)
        db = BurritoDbHelper(this)
        burritoList = db.getAllFavoriteBurritos()


        BottomNavbar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home ->{
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.cart ->{
                    val intent = Intent(this, CartDetailsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        burritoList = db.getAllFavoriteBurritos()
        adapter = BurritoAdapter(this, burritoList, this::AddBurritoToCart, this::navigateToDetailsScreen )
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun AddBurritoToCart(burrito: Burrito){
        if(!db.isBurritoInCart(burrito)){
            burrito.quantity= 1;
            db.addBurritoToCart(burrito)
        }
        Toast.makeText(this, "Added ${burrito.name} to Cart", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToDetailsScreen(burrito: Burrito) {
        val intent = Intent(this, BurritoDetailsActivity::class.java)
        intent.putExtra("BURRITO_OBJECT", burrito) // Replace "BURRITO_OBJECT" with the key you want to use
        startActivity(intent)
    }
}