package com.example.myburrito

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myburrito.adapter.BurritoAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.ArrayList
import android.os.Parcelable
import androidx.core.widget.addTextChangedListener
import com.example.myburrito.database.BurritoDbHelper
import com.google.android.material.textfield.TextInputEditText


class MainActivity : AppCompatActivity() {

    lateinit var RecyclerView : RecyclerView;
    lateinit var Adapter : BurritoAdapter;
    lateinit var burritoList : List<Burrito>;
    lateinit var LogoutImageView : ImageView;
    lateinit var BottomNavbar : BottomNavigationView;
    lateinit var selectedBurritos  : java.util.ArrayList<Burrito>
    lateinit var searchEditText: TextInputEditText
    lateinit var db : BurritoDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RecyclerView = findViewById(R.id.recyclerView_burrito_items);
        LogoutImageView = findViewById(R.id.imageView_logout)
        BottomNavbar = findViewById(R.id.bottom_navbar)
        searchEditText = findViewById(R.id.TextInput_search)
        burritoList = loadBurritosFromJson();
        selectedBurritos = ArrayList()
        db = BurritoDbHelper(this)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("username", null)

        Adapter = BurritoAdapter(this,  burritoList, this::AddBurritoToCart, this::navigateToDetailsScreen);
        val layoutManager = GridLayoutManager(this, 2)
        RecyclerView.layoutManager = layoutManager
        RecyclerView.adapter = Adapter

        LogoutImageView.setOnClickListener {
            // Remove username from SharedPreferences
            val sharedPreferences = getSharedPreferences("YOUR_PREFERENCE_NAME", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("username")
            editor.apply()

            // Redirect to the login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        BottomNavbar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.cart ->{
                    val intent = Intent(this, CartDetailsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.favorite ->{
                    startActivity(Intent(this, FavoriteBurritosActivity::class.java))
                    finish()
                    true
                }

                else -> {
                    false
                }
            }
        }
        searchEditText.addTextChangedListener {
            val query = it.toString().trim()
            val filteredBurritos = if (query.isEmpty()) {
                burritoList
            } else {
                burritoList.filter { burrito ->
                    burrito.name?.contains(query, ignoreCase = true) == true
                }
            }

            Adapter.updateBurritos(filteredBurritos)
        }
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

    fun loadBurritosFromJson(): List<Burrito> {
        val json: String
        try {
            val inputStream = this.assets.open("burritos.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return emptyList()
        }

        val typeToken = object : TypeToken<List<Burrito>>() {}.type
        return Gson().fromJson(json, typeToken)
    }
}