package com.example.myburrito

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.myburrito.database.BurritoDbHelper
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    lateinit var emailInputText :  TextInputEditText
    lateinit var passwordInputText :  TextInputEditText
    lateinit var loginButton: Button
    lateinit var signupButton: TextView
    lateinit var db: BurritoDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        emailInputText = findViewById(R.id.TextInput_email)
        passwordInputText = findViewById(R.id.TextInput_password)
        loginButton = findViewById(R.id.button_login)
        signupButton = findViewById(R.id.button_navigate_signup)

        db = BurritoDbHelper(this);
        loginButton.setOnClickListener {
            var email = emailInputText.editableText.toString()
            var password = passwordInputText.editableText.toString()
            if(!email.isEmpty() && !password.isEmpty()){
                val rowIndex : Long= db.authenticateUser(email, password);
                if(rowIndex != -1L){
                    val username = db.getUsernameById(rowIndex);
                    saveUsernameToSharedPreferences(username!!)
                    startActivity(Intent(this, MainActivity::class.java))
                }else{
                    Toast.makeText(this, "email or password are false!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Please fill all the fileds", Toast.LENGTH_SHORT).show()
            }
        }

        signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

    }

    private fun saveUsernameToSharedPreferences(username: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()
    }

}