package com.sumeet.realtimefirebase

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.sumeet.realtimefirebase.databinding.ActivityUpdateUserBinding
import java.util.UUID

class UpdateUserActivity : AppCompatActivity() {
    lateinit var updateUserBinding: ActivityUpdateUserBinding
    val database:FirebaseDatabase = FirebaseDatabase.getInstance()
    val myReference:DatabaseReference = database.reference.child("MyUsers")

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    var imageUri: Uri? = null
    val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageRef: StorageReference = firebaseStorage.reference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateUserBinding = ActivityUpdateUserBinding.inflate(layoutInflater)
        val view = updateUserBinding.root
        setContentView(view)

        supportActionBar?.title = "Update User"
        registerActivityForResult()



        getAndSetData()

        updateUserBinding.userButtonUpdate.setOnClickListener {
            uploadPhoto()
        }

        updateUserBinding.userProfileImageUpdate.setOnClickListener {
            chooseImage()
        }

    }

    fun chooseImage() {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)

    }

    fun registerActivityForResult() {
        activityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->
                    val resultCode = result.resultCode
                    val imageData = result.data
                    if (resultCode == RESULT_OK && imageData != null) {
                        imageUri = imageData.data
                        // Picasso
                        imageUri.let { Picasso.get().load(it).into(updateUserBinding.userProfileImageUpdate) }
                    }

                })
    }

    fun uploadPhoto() {
        updateUserBinding.userButtonUpdate.isClickable = false
        updateUserBinding.progressBarUpdate.visibility = View.VISIBLE


        // get the UUID
        val imageName = intent.getStringExtra("imageName").toString()
        val imageRef = storageRef.child("images").child(imageName)

        imageUri?.let { uri ->
            imageRef.putFile(uri).addOnSuccessListener {
                Toast.makeText(applicationContext,"Image Updated",Toast.LENGTH_SHORT).show()
                // downloadable url

                val myUploadedImageRef = storageRef.child("images").child(imageName)
                myUploadedImageRef.downloadUrl.addOnSuccessListener { url ->
                    val imageUrl = url.toString()
                    updateData(imageUrl,imageName)

                }

            }.addOnFailureListener {

                Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_SHORT).show()

            }
        }
    }


    fun getAndSetData() {
        val name = intent.getStringExtra("name")
        val age = intent.getIntExtra("age",0).toString()
        val email = intent.getStringExtra("email")
        val imageUrl = intent.getStringExtra("imageUrl").toString()


        updateUserBinding.editTextNameUpdate.setText(name)
        updateUserBinding.editTextAgeUpdate.setText(age)
        updateUserBinding.editTextEmailUpdate.setText(email)
        Picasso.get().load(imageUrl).into(updateUserBinding.userProfileImageUpdate)


    }

    fun updateData(imageUrl:String,imageName:String) {
        val updatedName = updateUserBinding.editTextNameUpdate.text.toString()
        val updatedAge = updateUserBinding.editTextAgeUpdate.text.toString().toInt()
        val updatedEmail = updateUserBinding.editTextEmailUpdate.text.toString()
        val userId = intent.getStringExtra("id")
        val userMap = mutableMapOf<String,Any>()

        //userMap["userId"] = userId.toString()
        userMap["userName"] = updatedName
        userMap["userAge"] = updatedAge
        userMap["userEmail"] = updatedEmail
        userMap["url"] = imageUrl
        userMap["imageName"] = imageName

        myReference.child(userId!!).updateChildren(userMap).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Toast.makeText(applicationContext,"The user has been updated",Toast.LENGTH_SHORT).show()
                updateUserBinding.userButtonUpdate.isClickable = true
                updateUserBinding.progressBarUpdate.visibility = View.INVISIBLE
                finish()
            }
            else {
                Toast.makeText(applicationContext,"Error",Toast.LENGTH_SHORT).show()
            }

        }
    }
}