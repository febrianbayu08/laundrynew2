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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.febrianbayu.modeldata.model_pegawai;


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference // For Realtime Database

    private val TAG = "LoginActivity" // For logging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        database = Firebase.database.reference // Initialize Firebase Realtime Database root reference

        val editTextEmail = findViewById<EditText>(R.id.editTextPhone) // Assuming this is for email
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val tvRegister = findViewById<TextView>(R.id.tv_register_prompt)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty()) {
                editTextEmail.error = "Email tidak boleh kosong"
                editTextEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                editTextPassword.error = "Password tidak boleh kosong"
                editTextPassword.requestFocus()
                return@setOnClickListener
            }

            signInUserAndFetchData(email, password)
        }
    }

    private fun signInUserAndFetchData(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        fetchPegawaiData(firebaseUser)
                    } else {
//                        Toast.makeText(baseContext, "Login Berhasil, namun user data tidak ditemukan.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Login Gagal: ${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun fetchPegawaiData(firebaseUser: FirebaseUser) {
        val userId = firebaseUser.uid
        // Assuming your Realtime Database structure is "pegawai/{userId}"
        val pegawaiRef = database.child("pegawai").child(userId)

        pegawaiRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val pegawai = snapshot.getValue<model_pegawai>() // Deserialize data to Pegawai object
                    if (pegawai != null) {
                        Log.d(TAG, "Pegawai data fetched: ${pegawai.namaPegawai}")
                        Toast.makeText(baseContext, "Login Berhasil. Selamat Datang ${pegawai.namaPegawai}", Toast.LENGTH_SHORT).show()

                        // Navigate to your main activity or dashboard
                        // You can pass the 'pegawai' object to the next activity if needed
                        val intent = Intent(this@LoginActivity, MainActivity::class.java) // Replace MainActivity
                        // Example: intent.putExtra("PEGAWAI_DATA", pegawai) // Make Pegawai Parcelable or Serializable if you pass it directly
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Log.w(TAG, "Pegawai data is null after deserialization.")
                        Toast.makeText(baseContext, "Login Berhasil, namun detail pegawai tidak lengkap.", Toast.LENGTH_LONG).show()
                        // Decide if you still want to proceed or handle this case differently
                        // For now, let's proceed to MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Log.w(TAG, "No data found for this pegawai at users/$userId")
                    Toast.makeText(baseContext, "Login Berhasil, namun tidak ada data pegawai di database.", Toast.LENGTH_LONG).show()
                    // Handle case where user is authenticated but has no corresponding entry in 'pegawai'
                    // You might still want to navigate or show a different screen
                    val intent = Intent(this@LoginActivity, MainActivity::class.java) // Or a profile setup screen
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read pegawai data.", error.toException())
                Toast.makeText(baseContext, "Gagal mengambil data pegawai: ${error.message}", Toast.LENGTH_LONG).show()
                // Handle error, maybe still proceed or show an error message and stay on login
            }
        })
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, you might want to directly fetch data and navigate
            // fetchPegawaiData(currentUser)
            // This prevents showing login screen if already logged in.
            // For now, let it go through the login button click to keep it simple.
        }
    }
}
