package com.supersamin.firebasemessanger

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerButton.setOnClickListener {
            doLogin()
        }

        returnTextView.setOnClickListener {
            Log.d("RegisterActivity","Try to show login activity")

            val intent = Intent(this, LoginActivity::class.java )
            startActivity(intent)
        }

        selectphoto_button.setOnClickListener {
            Log.d("RegisterActivity", "Try to show photo gallery")

            //load gallery
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("Register", "Photo selected")

            //get photo
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            //show in view
            selectphoto_circleview.setImageBitmap(bitmap)
            selectphoto_button.alpha = 0f

//            var bitmapDrawable = BitmapDrawable(bitmap)
//            selectphoto_button.setBackgroundDrawable(bitmapDrawable)
        }

    }

    private fun doLogin() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        Log.d("RegisterActivity", "Your email is $email")
        Log.d("RegisterActivity", "The password is $password")

        //check if empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter user email password", Toast.LENGTH_SHORT).show()
            return
        }

        //Firebaase registration
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //Register to Firebase database
                Log.d("RegisterActivity", "Succesfully create user ${it.result?.user?.uid}")

                // upload image to Firebase storage
                uploadImageToFirebaseStorage()

            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Error : ${it.message}")
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        //val ref = Firebase.storage.reference.child("images/$filename")
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        Log.d("RegisterActivity", "Uploading $selectedPhotoUri to $filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "File location: $it")
                    //save the information in Firebase database
                    saveToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener(){
                Log.d("RegisterActivity","Error occured ${it.message}")
            }
    }

    private fun saveToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, usernameEditText.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Yay... the data is stored in the Firebase database")

                val intent = Intent(this, LastMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
    }
}

class User(val uid: String, val username: String, val profileUrl: String){
    constructor() : this("","","")
}