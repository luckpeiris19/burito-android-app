package com.example.myburrito

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myburrito.database.BurritoDbHelper

class BurritoDetailsActivity : AppCompatActivity() {

    private lateinit var imageViewFavorite: ImageView
    private lateinit var burritoImage: ImageView
    private lateinit var getBack: ImageView
    private lateinit var tvBurritoTitle: TextView
    private lateinit var tvBurritoDescription: TextView
    private lateinit var checkboxDoubleCheese: CheckBox
    private lateinit var checkboxRice: CheckBox
    private lateinit var checkboxBeans: CheckBox
    private lateinit var checkboxOnions: CheckBox
    private lateinit var checkboxTomatoes: CheckBox
    private lateinit var checkboxSpicy: CheckBox
    private lateinit var buttonAddCustomBurritoToCart: Button
    private lateinit var tvBurritoPrice: TextView
    private lateinit var burritoDbHelper: BurritoDbHelper

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_burrito_details)
        initViews()
        burritoDbHelper = BurritoDbHelper(this)


        val burrito: Burrito? = intent.getParcelableExtra("BURRITO_OBJECT") as? Burrito
        if (burrito != null) {
            tvBurritoTitle.text = burrito.name
            tvBurritoPrice.text = burrito.price.toString() + "$"
            tvBurritoDescription.text = burrito.description

            Glide.with(this).asBitmap()
                .load(burrito.image)
                .centerCrop()
                .into(burritoImage)

            val isBurritoInFavorites = burritoDbHelper.isBurritoFavorite(burrito)
            if (isBurritoInFavorites == true) {
                imageViewFavorite.setImageDrawable(getDrawable(R.drawable.baseline_favorite_24))
            } else {
                imageViewFavorite.setImageDrawable(getDrawable(R.drawable.baseline_favorite_border_24))
            }
        }
        getBack.setOnClickListener {
            finish()
        }
        imageViewFavorite.setOnClickListener {
            if (burrito != null) {
                val isBurritoInFavorites =
                    burrito?.let { it1 -> burritoDbHelper.isBurritoFavorite(it1) }

                if (isBurritoInFavorites == true) {
                    // Burrito is in favorites, remove it
                    burritoDbHelper.deleteFavoriteBurrito(burrito)
                    imageViewFavorite.setImageDrawable(getDrawable(R.drawable.baseline_favorite_border_24))
                    Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                } else {
                    // Burrito is not in favorites, add it
                    burritoDbHelper.addFavoriteBurrito(burrito)
                    imageViewFavorite.setImageDrawable(getDrawable(R.drawable.baseline_favorite_24))
                    Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show()
                }
                imageViewFavorite.invalidate()

            }
        }

        buttonAddCustomBurritoToCart.setOnClickListener {
            val selectedIngredients = mutableListOf<String>()
            var updatedPrice = burrito?.price ?: 0.0
            var ingredients = HashMap<String, Boolean>()
            if (checkboxDoubleCheese.isChecked) {
                ingredients.set("Double Cheese", true)
                updatedPrice += 1.0 // Add price for double cheese
            }
            if (checkboxRice.isChecked) {
                ingredients.set("Rice", true)
                updatedPrice += 1.0 // Add price for rice
            }
            if (checkboxBeans.isChecked) {
                ingredients.set("Beans", true)
                updatedPrice += 1.0
            }
            if (checkboxOnions.isChecked) {
                ingredients.set("Onions", true)
                updatedPrice += 1.0
            }
            if (checkboxSpicy.isChecked) {
                ingredients.set("Spicy", true)
                updatedPrice += 1.0
            }

            if (checkboxTomatoes.isChecked) {
                ingredients.set("Tomatoes", true)
                updatedPrice += 1.0
            }


            if (ingredients.isNotEmpty()) {
                // Check if the exact burrito already exists in the database
                val existingBurrito = burritoDbHelper.isExactBurritoExist(burrito!!)

                if (existingBurrito) {
                    // Burrito already exists, update it with new details
                    burrito.price = updatedPrice
                    burrito.customizedIngredients = ingredients

                    // Update the existing burrito in the database
                    burritoDbHelper.updateBurrito(burrito)

                    Toast.makeText(this, "Updated custom burrito in cart", Toast.LENGTH_SHORT).show()
                } else {
                    // Burrito does not exist, create a new one and add it to the database
                    val customBurrito = Burrito(
                        name = burrito?.name,
                        price = updatedPrice,
                        description = burrito?.description,
                        image = burrito?.image,
                        quantity = 1,
                        customizedIngredients = ingredients
                    )

                    // Add the custom burrito to the database
                    burritoDbHelper.addBurritoToCart(customBurrito)

                    Toast.makeText(this, "Added custom burrito to cart", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please select at least one ingredient", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun initViews() {
        imageViewFavorite = findViewById(R.id.imageView_favorite)
        getBack = findViewById(R.id.imageView_get_back)
        burritoImage = findViewById(R.id.imageView_burrito)
        tvBurritoTitle = findViewById(R.id.tv_burrito_title)
        tvBurritoDescription = findViewById(R.id.tv_burrito_description)
        checkboxDoubleCheese = findViewById(R.id.checkbox_double_cheese)
        checkboxRice = findViewById(R.id.checkbox_rice)
        checkboxBeans = findViewById(R.id.checkbox_beans)
        checkboxOnions = findViewById(R.id.checkbox_onions)
        checkboxTomatoes = findViewById(R.id.checkbox_tomatos)
        checkboxSpicy = findViewById(R.id.checkbox_spicy)
        buttonAddCustomBurritoToCart = findViewById(R.id.button_add_custom_burrito_to_cart)
        tvBurritoPrice = findViewById(R.id.tv_burrito_price)


    }
}