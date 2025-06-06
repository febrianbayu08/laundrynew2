package com.febrianbayu.laundry

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.febrianbayu.modeldata.model_pegawai
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DataAkunActivity : AppCompatActivity() {

    private lateinit var tvIdPegawai: TextView
    private lateinit var tvNama: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvNoHp: TextView
    private lateinit var tvAlamat: TextView
    private lateinit var tvCabang: TextView
    private lateinit var btnLogout: Button

    private lateinit var sharedPref: SharedPreferences
    private val database = FirebaseDatabase.getInstance().getReference("pegawai")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_akun)

        sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        initViews()
        loadProfileData()
        setupListeners()
    }

    private fun initViews() {
        tvIdPegawai = findViewById(R.id.tv_akun_id_pegawai)
        tvNama = findViewById(R.id.tv_akun_nama)
        tvEmail = findViewById(R.id.tv_akun_email)
        tvNoHp = findViewById(R.id.tv_akun_nohp)
        tvAlamat = findViewById(R.id.tv_akun_alamat)
        tvCabang = findViewById(R.id.tv_akun_cabang)
        btnLogout = findViewById(R.id.btn_logout)
    }

    private fun loadProfileData() {
        val idPegawai = sharedPref.getString("idPegawai", null)
        if (idPegawai == null) {
            Toast.makeText(this, "Sesi tidak ditemukan, silakan login kembali.", Toast.LENGTH_LONG).show()
            logout()
            return
        }

        database.child(idPegawai).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val pegawai = snapshot.getValue(model_pegawai::class.java)
                    pegawai?.let {
                        tvIdPegawai.text = it.idPegawai ?: "N/A"
                        tvNama.text = it.namaPegawai ?: "N/A"
                        tvEmail.text = it.email ?: "N/A"
                        tvNoHp.text = it.noHPPegawai ?: "N/A"
                        tvAlamat.text = it.alamatPegawai ?: "N/A"
                        tvCabang.text = it.idCabang ?: "N/A"
                    }
                } else {
                    Toast.makeText(this@DataAkunActivity, "Gagal memuat data profil.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataAkunActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupListeners() {
        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
