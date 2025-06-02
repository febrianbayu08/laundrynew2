package com.febrianbayu.laundry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    // FirebaseAuth instance for handling user authentication
    private lateinit var auth: FirebaseAuth

    // Tag for logging messages
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge display for a more immersive UI
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth instance
        auth = Firebase.auth

        // Get references to UI elements
        val editTextEmail = findViewById<EditText>(R.id.editTextPhone) // Assuming this is for email
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val tvRegister = findViewById<TextView>(R.id.tv_register_prompt)

        // Apply window insets to adjust padding for system bars (e.g., status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set click listener for the register prompt text view
        tvRegister.setOnClickListener {
            // Create an Intent to navigate to the RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the login button
        buttonLogin.setOnClickListener {
            // Get email and password input from EditText fields
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            // Validate email input
            if (email.isEmpty()) {
                editTextEmail.error = "Email tidak boleh kosong"
                editTextEmail.requestFocus()
                return@setOnClickListener // Stop execution if email is empty
            }

            // Validate password input
            if (password.isEmpty()) {
                editTextPassword.error = "Password tidak boleh kosong"
                editTextPassword.requestFocus()
                return@setOnClickListener // Stop execution if password is empty
            }

            // Call the function to sign in the user with Firebase Authentication
            signInUser(email, password)
        }
    }

    /**
     * Attempts to sign in a user with the provided email and password using Firebase Authentication.
     * On successful login, navigates to MainActivity. On failure, displays an error message.
     * @param email The user's email address.
     * @param password The user's password.
     */
    private fun signInUser(email: String, password: String) {
        // Use Firebase Auth to sign in with email and password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, log the event
                    Log.d(TAG, "signInWithEmail:success")
                    // Display a success message to the user
                    Toast.makeText(baseContext, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                    // Create an Intent to navigate to MainActivity
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    // Clear the back stack so the user cannot return to the login screen by pressing back
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent) // Start MainActivity
                    finish() // Finish LoginActivity so it's removed from the back stack
                } else {
                    // If sign in fails, log the error and display a toast message
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Login Gagal: ${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    /**
     * Called when the activity is starting or resuming.
     * Checks if a user is already signed in. If so, it directly navigates to MainActivity,
     * preventing the user from seeing the login screen again.
     */
    public override fun onStart() {
        super.onStart()
        // Get the currently signed-in user
        val currentUser = auth.currentUser
        // If there is a current user, it means they are already logged in
        if (currentUser != null) {
            Log.d(TAG, "User already logged in: ${currentUser.uid}")
            // Navigate to MainActivity
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            // Clear the back stack
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Finish LoginActivity
        }
    }
}
