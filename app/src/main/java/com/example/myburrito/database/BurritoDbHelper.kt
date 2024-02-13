package com.example.myburrito.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myburrito.Burrito
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BurritoDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "burrito_database"
        private const val DATABASE_VERSION = 1

        // Table name and column names
        private const val TABLE_USERS = "users"
        private const val TABLE_BURRITOS = "burritos"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_BURRITO_ID = "burrito_id"
        private const val COLUMN_BURRITO_NAME = "name"
        private const val COLUMN_BURRITO_PRICE = "price"
        private const val COLUMN_BURRITO_DESCRIPTION = "description"
        private const val COLUMN_BURRITO_IMAGE = "image"
        private const val COLUMN_BURRITO_QUANTITY = "quantity"
        private const val COLUMN_BURRITO_CUSTOMIZED_INGREDIENTS = "customized_ingredients"
        private const val COLUMN_BURRITO_IN_CART = "in_cart"
        private const val COLUMN_BURRITO_IS_FAVORITE = "is_favorite"

    }

    // SQL statement to create the users table
    private val CREATE_USERS_TABLE = """
        CREATE TABLE $TABLE_USERS (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_USERNAME TEXT,
            $COLUMN_EMAIL TEXT,
            $COLUMN_PASSWORD TEXT
        )
    """.trimIndent()

    private val CREATE_BURRITOS_TABLE = """
        CREATE TABLE $TABLE_BURRITOS (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_BURRITO_NAME TEXT,
            $COLUMN_BURRITO_PRICE REAL,
            $COLUMN_BURRITO_DESCRIPTION TEXT,
            $COLUMN_BURRITO_IMAGE TEXT,
            $COLUMN_BURRITO_QUANTITY INTEGER,
            $COLUMN_BURRITO_CUSTOMIZED_INGREDIENTS TEXT, 
            $COLUMN_BURRITO_IN_CART INTEGER DEFAULT 0,
        $COLUMN_BURRITO_IS_FAVORITE INTEGER DEFAULT 0
        )
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_USERS_TABLE)
        db?.execSQL(CREATE_BURRITOS_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades here if needed
    }

    fun deleteFavoriteBurrito(burrito: Burrito) {
        val db = writableDatabase
        db.delete(TABLE_BURRITOS, "$COLUMN_BURRITO_NAME = ?", arrayOf(burrito.name))
        db.close()
    }

    @SuppressLint("Range")
    fun getAllBurritos(): List<Burrito> {
        val burritos = mutableListOf<Burrito>()

        // Select all rows from the favorites table
        val query = "SELECT * FROM $TABLE_BURRITOS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        // Iterate through the cursor and add burritos to the list
        while (cursor.moveToNext()) {
            val burrito = Burrito(
                name = cursor.getString(cursor.getColumnIndex(COLUMN_BURRITO_NAME)),
                price = cursor.getDouble(cursor.getColumnIndex(COLUMN_BURRITO_PRICE)),
                description = cursor.getString(cursor.getColumnIndex(COLUMN_BURRITO_DESCRIPTION)),
                image = cursor.getString(cursor.getColumnIndex(COLUMN_BURRITO_IMAGE)),
                quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_BURRITO_QUANTITY)),
                customizedIngredients = null,  // You may need to modify this based on your implementation
                inCart = cursor.getInt(cursor.getColumnIndex(COLUMN_BURRITO_IN_CART)) == 1,
                isFavorite = cursor.getInt(cursor.getColumnIndex(COLUMN_BURRITO_IS_FAVORITE)) == 1
            )
            burritos.add(burrito)
        }

        // Close the cursor and database
        cursor.close()
        db.close()

        return burritos
    }

    // Function to check if a burrito is in the favorites table
    fun isBurritoFavorite(burrito: Burrito): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_BURRITOS WHERE $COLUMN_BURRITO_NAME=? AND $COLUMN_BURRITO_IS_FAVORITE=1",
            arrayOf(burrito.name)
        )
        val isInFavorites = cursor.count > 0
        cursor.close()
        db.close()
        return isInFavorites
    }

    fun addFavoriteBurrito(burrito: Burrito) {
        val db = writableDatabase

        // Check if the burrito is already in the database
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_BURRITOS WHERE $COLUMN_BURRITO_NAME=?",
            arrayOf(burrito.name)
        )

        if (cursor.moveToFirst()) {
            // Burrito is already in the database, update isFavorite to true
            val values = ContentValues().apply {
                put(COLUMN_BURRITO_IS_FAVORITE, 1)
            }
            db.update(
                TABLE_BURRITOS,
                values,
                "$COLUMN_BURRITO_NAME=?",
                arrayOf(burrito.name)
            )
        } else {
            // Burrito is not in the database, insert it with isFavorite set to true
            val values = ContentValues().apply {
                put(COLUMN_BURRITO_NAME, burrito.name)
                put(COLUMN_BURRITO_PRICE, burrito.price)
                put(COLUMN_BURRITO_DESCRIPTION, burrito.description)
                put(COLUMN_BURRITO_IMAGE, burrito.image)
                put(COLUMN_BURRITO_QUANTITY, burrito.quantity)
                put(COLUMN_BURRITO_CUSTOMIZED_INGREDIENTS, burrito.customizedIngredients.toString())
                put(COLUMN_BURRITO_IN_CART, 0)
                put(COLUMN_BURRITO_IS_FAVORITE, 1)
            }
            db.insert(TABLE_BURRITOS, null, values)
        }

        cursor.close()
        db.close()
    }

    @SuppressLint("Range")
    fun getAllCartBurritos(): List<Burrito> {
        val burritos = mutableListOf<Burrito>()
        val gson = Gson()

        // Select all rows from the burritos table where inCart is true
        val query = "SELECT * FROM $TABLE_BURRITOS WHERE $COLUMN_BURRITO_IN_CART=1"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        // Iterate through the cursor and add burritos to the list
        while (cursor.moveToNext()) {
            val jsonFromDatabase = cursor.getString(cursor.getColumnIndex(COLUMN_BURRITO_CUSTOMIZED_INGREDIENTS))
            val type = object : TypeToken<HashMap<String, Boolean>>() {}.type
            val customizedIngredients = gson.fromJson<HashMap<String, Boolean>>(jsonFromDatabase, type)

            val burrito = Burrito(
                name = cursor.getString(cursor.getColumnIndex(COLUMN_BURRITO_NAME)),
                price = cursor.getDouble(cursor.getColumnIndex(COLUMN_BURRITO_PRICE)),
                description = cursor.getString(cursor.getColumnIndex(COLUMN_BURRITO_DESCRIPTION)),
                image = cursor.getString(cursor.getColumnIndex(COLUMN_BURRITO_IMAGE)),
                quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_BURRITO_QUANTITY)),
                customizedIngredients = customizedIngredients // You may need to modify this based on your implementation
            )
            burritos.add(burrito)
        }

        // Close the cursor and database
        cursor.close()
        db.close()

        return burritos
    }

    fun removeAllBurritosFromCart() {
        val db = writableDatabase
        db.delete(TABLE_BURRITOS, "$COLUMN_BURRITO_IN_CART = ?", arrayOf("1"))
        db.close()
    }

    @SuppressLint("Range")
    fun getAllFavoriteBurritos(): List<Burrito> {
        val burritos = mutableListOf<Burrito>()
        val gson = Gson()

        // Select all rows from the burritos table where isFavorite is true
        val query = "SELECT * FROM $TABLE_BURRITOS WHERE $COLUMN_BURRITO_IS_FAVORITE=1"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)

        // Iterate through the cursor and add burritos to the list
        while (cursor.moveToNext()) {

            val jsonFromDatabase = cursor.getString(cursor.getColumnIndex(COLUMN_BURRITO_CUSTOMIZED_INGREDIENTS))
            val type = object : TypeToken<HashMap<String, Boolean>>() {}.type
            val customizedIngredients = gson.fromJson<HashMap<String, Boolean>>(jsonFromDatabase, type)

            val burrito = Burrito(
                name = cursor.getString(cursor.getColumnIndex(COLUMN_BURRITO_NAME)),
                price = cursor.getDouble(cursor.getColumnIndex(COLUMN_BURRITO_PRICE)),
                description = cursor.getString(cursor.getColumnIndex(COLUMN_BURRITO_DESCRIPTION)),
                image = cursor.getString(cursor.getColumnIndex(COLUMN_BURRITO_IMAGE)),
                quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_BURRITO_QUANTITY)),
                customizedIngredients = customizedIngredients  // You may need to modify this based on your implementation
            )
            burritos.add(burrito)
        }

        // Close the cursor and database
        cursor.close()
        db.close()

        return burritos
    }

    fun isBurritoInCart(burrito: Burrito): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_BURRITOS WHERE $COLUMN_BURRITO_NAME=?",
            arrayOf(burrito.name)
        )
        val isInCart = cursor.count > 0
        cursor.close()
        db.close()
        return isInCart
    }

    fun addBurritoToCart(burrito: Burrito) {
        val db = writableDatabase
        var jsonIngrediants = ""
        if(burrito.customizedIngredients!=null){
            val gson = Gson()
            jsonIngrediants = gson.toJson(burrito.customizedIngredients)
        }
        val values = ContentValues().apply {
            put(COLUMN_BURRITO_NAME, burrito.name)
            put(COLUMN_BURRITO_PRICE, burrito.price)
            put(COLUMN_BURRITO_DESCRIPTION, burrito.description)
            put(COLUMN_BURRITO_IMAGE, burrito.image)
            put(COLUMN_BURRITO_QUANTITY, burrito.quantity)
            put(
                COLUMN_BURRITO_CUSTOMIZED_INGREDIENTS, jsonIngrediants
            ) // Convert HashMap to String
            put(COLUMN_BURRITO_IN_CART, 1)
            put(COLUMN_BURRITO_IS_FAVORITE, 0)
        }

        db.insert(TABLE_BURRITOS, null, values)


        db.close()
    }

    fun deleteBurrito(burrito: Burrito) {
        val db = writableDatabase
        db.delete(TABLE_BURRITOS, "$COLUMN_BURRITO_NAME = ?", arrayOf(burrito.name))
        db.close()
    }

    fun updateBurritoQuantity(burrito: Burrito, newQuantity: Int) {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_BURRITO_QUANTITY, newQuantity)
        }

        db.update(TABLE_BURRITOS, values, "$COLUMN_BURRITO_NAME=?", arrayOf(burrito.name))
        db.close()
    }

    fun updateBurrito(burrito: Burrito) {
        val db = writableDatabase
        var jsonIngrediants =""
        if(burrito.customizedIngredients!=null){
            val gson = Gson()
             jsonIngrediants = gson.toJson(burrito.customizedIngredients)
        }

        val values = ContentValues().apply {
            put(COLUMN_BURRITO_NAME, burrito.name)
            put(COLUMN_BURRITO_PRICE, burrito.price)
            put(COLUMN_BURRITO_DESCRIPTION, burrito.description)
            put(COLUMN_BURRITO_IMAGE, burrito.image)
            put(COLUMN_BURRITO_QUANTITY, burrito.quantity)
            put(COLUMN_BURRITO_CUSTOMIZED_INGREDIENTS,  jsonIngrediants) // Convert HashMap to String

        }

        db.update(
            TABLE_BURRITOS,
            values,
            "$COLUMN_BURRITO_NAME = ?",
            arrayOf(burrito.name)
        )

        db.close()
    }

    fun isExactBurritoExist(burrito: Burrito): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_BURRITOS WHERE $COLUMN_BURRITO_NAME=? AND $COLUMN_BURRITO_CUSTOMIZED_INGREDIENTS=?",
            arrayOf(burrito.name, burrito.customizedIngredients.toString())
        )
        val isExist = cursor.count > 0
        cursor.close()
        db.close()
        return isExist
    }

    fun insertUser(username: String, email: String, password: String): Long {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }

        return db.insert(TABLE_USERS, null, values)
    }


    @SuppressLint("Range")
    fun authenticateUser(email: String, password: String): Long {
        val db = readableDatabase
        val columns = arrayOf(COLUMN_ID)
        val selection = "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(email, password)

        val cursor: Cursor =
            db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)
        val userExists = cursor.moveToFirst()

        val userId: Long = if (userExists) {
            // User exists, retrieve the user's ID
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
        } else {
            // User doesn't exist or credentials are incorrect
            -1
        }

        cursor.close()

        return userId
    }

    @SuppressLint("Range")
    fun getUsernameById(userId: Long): String? {
        val db = readableDatabase
        val columns = arrayOf(COLUMN_USERNAME)
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf(userId.toString())

        val cursor: Cursor =
            db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null)
        val username: String? = if (cursor.moveToFirst()) {
            // User found, retrieve the username
            cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME))
        } else {
            // User not found
            null
        }

        cursor.close()

        return username
    }


}
