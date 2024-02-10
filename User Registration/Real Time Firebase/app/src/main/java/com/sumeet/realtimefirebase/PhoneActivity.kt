package com.sumeet.realtimefirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.sumeet.realtimefirebase.databinding.ActivityPhoneBinding
import java.util.concurrent.TimeUnit

class PhoneActivity : AppCompatActivity() {
    lateinit var phoneBinding:ActivityPhoneBinding
    val auth:FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var mCallbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phoneBinding = ActivityPhoneBinding.inflate(layoutInflater)
        val view = phoneBinding.root
        setContentView(view)

        phoneBinding.sendCodeButton.setOnClickListener {
            val userPhoneNumber = phoneBinding.editTextPhone.text.toString()
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(userPhoneNumber)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(this@PhoneActivity)
                .setCallbacks(mCallbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }

        phoneBinding.verifyButton.setOnClickListener {
            loginWithSMSCode()
        }

        mCallbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(p0: FirebaseException) {

            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationCode = p0

            }

        }
    }

    fun loginWithSMSCode() {
        val userEnterCode = phoneBinding.editTextNumber.text.toString()
        val credential = PhoneAuthProvider.getCredential(verificationCode,userEnterCode)
        loginWithPhoneAuthCredential(credential)

    }

    fun loginWithPhoneAuthCredential(credential:PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val intent = Intent(this@PhoneActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(applicationContext,"The Code you entered is incorrect",Toast.LENGTH_SHORT).show()

            }
        }
    }
}