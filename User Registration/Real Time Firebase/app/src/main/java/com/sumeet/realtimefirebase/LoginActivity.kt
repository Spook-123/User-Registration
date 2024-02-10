package com.sumeet.realtimefirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sumeet.realtimefirebase.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()


        loginBinding.loginButton.setOnClickListener {
            val userEmail = loginBinding.editTextEmailLogin.text.toString()
            val userPassword = loginBinding.editTextPasswordLogin.text.toString()
            signinWithFirebase(userEmail, userPassword)

        }

        loginBinding.signupButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }

        loginBinding.forgotPasswordButton.setOnClickListener {
            val intent = Intent(this,ForgetActivity::class.java)
            startActivity(intent)
        }

        loginBinding.loginWithPhoneButton.setOnClickListener {
            val intent = Intent(this@LoginActivity,PhoneActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun signinWithFirebase(userEmail: String, userPassword: String) {
       auth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener { task ->
           if(task.isSuccessful) {
               Toast.makeText(applicationContext,"Login Successfully",Toast.LENGTH_SHORT).show()
               val intent = Intent(this@LoginActivity,MainActivity::class.java)
               startActivity(intent)
               finish()
           }
           else{
               Log.e("Error Login", task.exception?.toString()!!)
               Toast.makeText(applicationContext,task.exception?.toString(),Toast.LENGTH_SHORT).show()
           }

       }

    }

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser

        if(user != null) {
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
