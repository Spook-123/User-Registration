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
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.sumeet.realtimefirebase.databinding.ActivityAddUserBinding
import java.util.UUID

class AddUserActivity : AppCompatActivity() {

    lateinit var addUserBinding: ActivityAddUserBinding
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myReference: DatabaseReference = database.reference.child("MyUsers")
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    var imageUri: Uri? = null
    val firebaseStorage:FirebaseStorage = FirebaseStorage.getInstance()
    val storageRef:StorageReference = firebaseStorage.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addUserBinding = ActivityAddUserBinding.inflate(layoutInflater)
        val view = addUserBinding.root
        setContentView(view)

        supportActionBar?.title = "Add User"

        // register the intent
        registerActivityForResult()


        addUserBinding.addUserButton.setOnClickListener {
            uploadPhoto()
        }

        addUserBinding.userProfileImage.setOnClickListener {
            chooseImage()
        }
    }


    fun chooseImage() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)

        }
    }

    fun registerActivityForResult() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->
                    val resultCode = result.resultCode
                    val imageData = result.data
                    if (resultCode == RESULT_OK && imageData != null) {
                        imageUri = imageData.data
                        // Picasso
                        imageUri.let { Picasso.get().load(it).into(addUserBinding.userProfileImage) }
                    }

                })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }
    }

    fun addUserToDatabase(url:String,imageName:String) {
        val name: String = addUserBinding.editTextName.text.toString()
        val age: Int = addUserBinding.editTextAge.text.toString().toInt()
        val email: String = addUserBinding.editTextEmail.toString()

        val id: String = myReference.push().key.toString()

        val user = Users(id, name, age, email,url,imageName)

        myReference.child(id).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    "The new user has been added to the database",
                    Toast.LENGTH_SHORT
                ).show()
                addUserBinding.addUserButton.isClickable = true
                addUserBinding.progressBar.visibility = View.INVISIBLE
                finish()
            } else {
                Toast.makeText(applicationContext, task.exception.toString(), Toast.LENGTH_SHORT)
                    .show()

            }
        }


    }

    fun uploadPhoto() {
        addUserBinding.addUserButton.isClickable = false
        addUserBinding.progressBar.visibility = View.VISIBLE


        // UUID
        val imageName = UUID.randomUUID().toString()

        val imageRef = storageRef.child("images").child(imageName)

        imageUri?.let { uri ->
            imageRef.putFile(uri).addOnSuccessListener {
                Toast.makeText(applicationContext,"Image Uploaded",Toast.LENGTH_SHORT).show()
                // downloadable url

                val myUploadedImageRef = storageRef.child("images").child(imageName)
                myUploadedImageRef.downloadUrl.addOnSuccessListener { url ->
                    val imageUrl = url.toString()
                    addUserToDatabase(imageUrl,imageName)

                }

            }.addOnFailureListener {

                Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_SHORT).show()

            }
        }
    }
}