package com.febrianbayu.laundry // Sesuaikan dengan package project Anda

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.febrianbayu.laundry.databinding.ActivityRegisterBinding // Impor ViewBinding, pastikan nama file XML benar
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        binding.tvLoginPrompt.setOnClickListener {
            // Kembali ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java) // Pastikan LoginActivity adalah nama kelas yang benar
            startActivity(intent)
            finish() // Selesaikan RegisterActivity agar tidak bisa kembali dengan tombol back
        }
    }

    private fun registerUser() {
        val email = binding.etEmailRegister.text.toString().trim()
        val password = binding.etPasswordRegister.text.toString().trim()
        val confirmPassword = binding.etConfirmPasswordRegister.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmailRegister.error = "Email harus diisi"
            binding.etEmailRegister.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailRegister.error = "Masukkan email yang valid"
            binding.etEmailRegister.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.etPasswordRegister.error = "Password harus diisi"
            binding.etPasswordRegister.requestFocus()
            return
        }

        if (password.length < 6) {
            binding.etPasswordRegister.error = "Password minimal 6 karakter"
            binding.etPasswordRegister.requestFocus()
            return
        }

        if (confirmPassword.isEmpty()) {
            binding.etConfirmPasswordRegister.error = "Konfirmasi password harus diisi"
            binding.etConfirmPasswordRegister.requestFocus()
            return
        }

        if (password != confirmPassword) {
            binding.etConfirmPasswordRegister.error = "Password tidak cocok"
            binding.etConfirmPasswordRegister.requestFocus()
            return
        }

        binding.pbRegister.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false
        binding.tvLoginPrompt.isEnabled = false

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.pbRegister.visibility = View.GONE
                binding.btnRegister.isEnabled = true
                binding.tvLoginPrompt.isEnabled = true

                if (task.isSuccessful) {
                    Log.d("RegisterActivity", "createUserWithEmail:success")
                    Toast.makeText(baseContext, "Registrasi berhasil. Silakan login.", Toast.LENGTH_LONG).show()

                    // Arahkan ke LoginActivity
                    val intent = Intent(this, LoginActivity::class.java) // Pastikan LoginActivity benar
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Hapus back stack
                    startActivity(intent)
                    finish()

                } else {
                    Log.w("RegisterActivity", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Registrasi gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
