package com.sumeet.realtimefirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.sumeet.realtimefirebase.databinding.ActivityForgetBinding

class ForgetActivity : AppCompatActivity() {
    lateinit var forgetBinding:ActivityForgetBinding

    val auth:FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgetBinding = ActivityForgetBinding.inflate(layoutInflater)
        val view = forgetBinding.root
        setContentView(view)


        forgetBinding.resetPasswordButton.setOnClickListener {
            val email = forgetBinding.editTextEmailForget.text.toString()
            auth.sendPasswordResetEmail(email).addOnCompleteListener { task->
                if(task.isSuccessful) {
                    Toast.makeText(applicationContext,"We Sent a Password reset mail to your mail address",Toast.LENGTH_SHORT).show()
                    finish()
                }
                else {
                    Toast.makeText(applicationContext,task.exception?.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}