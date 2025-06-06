package com.febrianbayu.laundry

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.febrianbayu.modeldata.model_pegawai
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        val editTextEmail = findViewById<EditText>(R.id.editTextPhone)
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

            signInUser(email, password)
        }
    }

    private fun signInUser(email: String, password: String) {
        val pegawaiRef = FirebaseDatabase.getInstance().getReference("pegawai")
        val query = pegawaiRef.orderByChild("email").equalTo(email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var userFound = false
                    for (userSnapshot in snapshot.children) {
                        val pegawai = userSnapshot.getValue(model_pegawai::class.java)
                        if (pegawai != null && pegawai.password == password) {
                            userFound = true
                            Log.d(TAG, "Login successful for email: $email")
                            Toast.makeText(baseContext, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                            // Save user session
                            val editor = sharedPref.edit()
                            editor.putString("idPegawai", pegawai.idPegawai)
                            editor.putString("namaPegawai", pegawai.namaPegawai)
                            editor.putString("idCabang", pegawai.idCabang)
                            editor.putBoolean("isLoggedIn", true)
                            editor.apply()

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                            break // Exit loop once user is found and verified
                        }
                    }
                    if (!userFound) {
                        Log.w(TAG, "Login failed: Incorrect password for email: $email")
                        Toast.makeText(baseContext, "Login Gagal: Password salah.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.w(TAG, "Login failed: User not found for email: $email")
                    Toast.makeText(baseContext, "Login Gagal: Pengguna tidak ditemukan.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database query cancelled: ${error.message}")
                Toast.makeText(baseContext, "Login Gagal: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is already logged in via SharedPreferences
        if (sharedPref.getBoolean("isLoggedIn", false)) {
            Log.d(TAG, "User already logged in, redirecting to MainActivity.")
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
