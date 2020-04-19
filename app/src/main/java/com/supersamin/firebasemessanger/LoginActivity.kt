package com.supersamin.firebasemessanger

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        registerButton.setOnClickListener {

            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            Log.d("LogActivity", "Email is $email")
            Log.d("LogActivity", "Password is $password")

            //Firebase login
            FirebaseAuth.getInstance().signInWithEmailAndPassword("$email", "$password")
        }

        returnTextView.setOnClickListener {
            finish()
        }
    }
}
