package com.febrianbayu.laundry

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.febrianbayu.cabang.DataCabangActivity
import com.febrianbayu.layanan.DataLayananActivity
import com.febrianbayu.pegawai.DataPegawaiActivity
import com.febrianbayu.pelanggan.DataPelangganActivity
import com.febrianbayu.tambahan.DataTambahanActivity
import com.febrianbayu.transaksi.DataTransaksiActivity
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvDate = findViewById<TextView>(R.id.tvDate)
        tvDate.text = getCurrentDate()

        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        tvGreeting.text = getGreetingMessage()

        val pelangganMenu = findViewById<LinearLayout>(R.id.pelanggan_menu)
        pelangganMenu.setOnClickListener {
            val intent = Intent(this, DataPelangganActivity::class.java)
            startActivity(intent)
        }

        val pegawaiMenu = findViewById<LinearLayout>(R.id.pegawai_menu)
        pegawaiMenu.setOnClickListener {
            val intent = Intent(this, DataPegawaiActivity::class.java)
            startActivity(intent)
        }

        val layananMenu = findViewById<LinearLayout>(R.id.layanan_menu)
        layananMenu.setOnClickListener {
            val intent = Intent(this, DataLayananActivity::class.java)
            startActivity(intent)
        }

        val cabangMenu = findViewById<LinearLayout>(R.id.cabang_menu)
        cabangMenu.setOnClickListener {
            val intent = Intent(this, DataCabangActivity::class.java)
            startActivity(intent)
        }

        val tambahanMenu = findViewById<LinearLayout>(R.id.tambahan_menu)
        tambahanMenu.setOnClickListener {
            val intent = Intent(this, DataTambahanActivity::class.java)
            startActivity(intent)
        }

        val transaksiMenu = findViewById<LinearLayout>(R.id.transaksi_menu)
        transaksiMenu.setOnClickListener {
            val intent = Intent(this, DataTransaksiActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // If no user is signed in, redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun getGreetingMessage(): String {
        val currentTime = LocalTime.now()
        return when {
            currentTime.hour in 5..10 -> "Selamat Pagi"
            currentTime.hour in 11..14 -> "Selamat Siang"
            currentTime.hour in 15..18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    private fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        return currentDate.format(formatter)
    }
    
}
