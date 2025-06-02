package com.febrianbayu.transaksi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.febrianbayu.laundry.R
import com.febrianbayu.modeldata.model_tambahan
import com.febrianbayu.modeldata.model_transaksi
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KonfirmasiDataActivity : AppCompatActivity() {

    private lateinit var tvNamaPelanggan: TextView
    private lateinit var tvNoHP: TextView
    private lateinit var tvLayanan: TextView
    private lateinit var tvHargaLayanan: TextView
    private lateinit var rvLayananTambahan: RecyclerView
    private lateinit var tvTotalBayar: TextView
    private lateinit var btnBatal: Button
    private lateinit var btnPembayaran: Button

    private var listLayananTambahan = ArrayList<model_tambahan>()
    private lateinit var layananTambahanAdapter: KonfirmasiLayananTambahanAdapter

    private var namaPelanggan: String = ""
    private var noHP: String = ""
    private var idPelanggan: String = ""
    private var namaLayanan: String = ""
    private var hargaLayanan: String = ""
    private var idLayanan: String = ""
    private var idCabang: String = ""
    private var namaCabang: String = ""
    private var idPegawai: String = ""
    private var namaPegawai: String = ""
    private var totalHarga: String = ""

    // Firebase database reference
    private lateinit var database: FirebaseDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_konfirmasi_data)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()

        // Initialize views
        tvNamaPelanggan = findViewById(R.id.tvNamaPelanggan)
        tvNoHP = findViewById(R.id.tvNoHP)
        tvLayanan = findViewById(R.id.tvLayanan)
        tvHargaLayanan = findViewById(R.id.tvHargaLayanan)
        rvLayananTambahan = findViewById(R.id.rvLayananTambahan)
        tvTotalBayar = findViewById(R.id.tvTotalBayar)
        btnBatal = findViewById(R.id.btnBatal)
        btnPembayaran = findViewById(R.id.btnPembayaran)

        // Get data from intent
        getIntentData()

        // Setup RecyclerView
        setupRecyclerView()

        // Set data to views
        setDataToViews()

        // Button click listeners
        btnBatal.setOnClickListener {
            finish()
        }

        btnPembayaran.setOnClickListener {
            showPaymentMethodBottomSheet()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getIntentData() {
        // Get data from intent
        namaPelanggan = intent.getStringExtra("namaPelanggan") ?: ""
        noHP = intent.getStringExtra("noHP") ?: ""
        idPelanggan = intent.getStringExtra("idPelanggan") ?: ""
        namaLayanan = intent.getStringExtra("namaLayanan") ?: ""
        hargaLayanan = intent.getStringExtra("hargaLayanan") ?: ""
        idLayanan = intent.getStringExtra("idLayanan") ?: ""
        idCabang = intent.getStringExtra("idCabang") ?: ""
        namaCabang = intent.getStringExtra("namaCabang") ?: ""
        idPegawai = intent.getStringExtra("idPegawai") ?: ""
        namaPegawai = intent.getStringExtra("namaPegawai") ?: ""
        totalHarga = intent.getStringExtra("totalHarga") ?: "0"

        // Get list of additional services
        if (intent.hasExtra("layananTambahan")) {
            val tambahanFromIntent = intent.getParcelableArrayListExtra<model_tambahan>("layananTambahan")
            if (tambahanFromIntent != null) {
                listLayananTambahan.addAll(tambahanFromIntent)
            }
        }
    }

    private fun setupRecyclerView() {
        layananTambahanAdapter = KonfirmasiLayananTambahanAdapter(listLayananTambahan)
        val layoutManager = LinearLayoutManager(this)
        rvLayananTambahan.layoutManager = layoutManager
        rvLayananTambahan.adapter = layananTambahanAdapter
    }

    private fun setDataToViews() {
        // Set customer data
        tvNamaPelanggan.text = namaPelanggan
        tvNoHP.text = noHP

        // Set service data
        tvLayanan.text = namaLayanan
        tvHargaLayanan.text = formatRupiah(hargaLayanan)

        // Set total price
        tvTotalBayar.text = formatRupiah(totalHarga)
    }

    private fun formatRupiah(amount: String): String {
        try {
            val price = amount.replace("Rp", "").replace(".", "").replace(",00", "").trim().toDouble()
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            return formatRupiah.format(price)
        } catch (e: Exception) {
            Log.e("KonfirmasiDataActivity", "Error formatting rupiah: ${e.message}")
            return "Rp$amount"
        }
    }

    private fun showPaymentMethodBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_payment_method, null)

        // Find payment method buttons
        val btnBayarNanti = view.findViewById<Button>(R.id.btnBayarNanti)
        val btnTunai = view.findViewById<Button>(R.id.btnTunai)
        val btnQRIS = view.findViewById<Button>(R.id.btnQRIS)
        val btnDANA = view.findViewById<Button>(R.id.btnDANA)
        val btnGoPay = view.findViewById<Button>(R.id.btnGoPay)
        val btnOVO = view.findViewById<Button>(R.id.btnOVO)
        val btnBatal = view.findViewById<Button>(R.id.btnBatalPembayaran)

        // Set click listeners
        btnBayarNanti.setOnClickListener {
            processPayment("BAYAR_NANTI")
            dialog.dismiss()
        }

        btnTunai.setOnClickListener {
            processPayment("TUNAI")
            dialog.dismiss()
        }

        btnQRIS.setOnClickListener {
            processPayment("QRIS")
            dialog.dismiss()
        }

        btnDANA.setOnClickListener {
            processPayment("DANA")
            dialog.dismiss()
        }

        btnGoPay.setOnClickListener {
            processPayment("GOPAY")
            dialog.dismiss()
        }

        btnOVO.setOnClickListener {
            processPayment("OVO")
            dialog.dismiss()
        }

        btnBatal.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun processPayment(paymentMethod: String) {
        try {
            // Generate a transaction ID
            val idTransaksi = "TR" + System.currentTimeMillis()

            // Create transaction model
            val transaksi = model_transaksi(
                idTransaksi = idTransaksi,
                idPelanggan = idPelanggan,
                namaPelanggan = namaPelanggan,
                noHP = noHP,
                idLayanan = idLayanan,
                namaLayanan = namaLayanan,
                hargaLayanan = hargaLayanan,
                idCabang = idCabang,
                namaCabang = namaCabang,
                idPegawai = idPegawai,
                namaPegawai = namaPegawai,
                tanggalTransaksi = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                statusPembayaran = if (paymentMethod == "BAYAR_NANTI") "BELUM_DIBAYAR" else "LUNAS",
                statusPesanan = "DIPROSES",
                metodePembayaran = paymentMethod,
                layananTambahan = listLayananTambahan,
                totalHarga = totalHarga
            )

            // Save transaction to Firebase Realtime Database
            saveTransactionToFirebase(transaksi)

        } catch (e: Exception) {
            Log.e("KonfirmasiDataActivity", "Error processing payment: ${e.message}")
            Toast.makeText(
                this,
                "Gagal memproses pembayaran: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveTransactionToFirebase(transaksi: model_transaksi) {
        // Show loading toast
        Toast.makeText(this, "Menyimpan transaksi...", Toast.LENGTH_SHORT).show()

        // Get reference to 'transaksi' node in database
        val transactionRef = database.reference.child("transaksi")

        // Save transaction to Firebase
        transactionRef.child(transaksi.idTransaksi).setValue(transaksi)
            .addOnSuccessListener {
                // Transaction saved successfully
                Toast.makeText(
                    this,
                    "Pembayaran berhasil dengan metode: ${transaksi.metodePembayaran}",
                    Toast.LENGTH_SHORT
                ).show()

                // Navigate to receipt screen
                val intent = Intent(this, TransaksiSelesaiActivity::class.java)
                intent.putExtra("idTransaksi", transaksi.idTransaksi)
                intent.putExtra("metodePembayaran", transaksi.metodePembayaran)
                intent.putExtra("namaPelanggan", transaksi.namaPelanggan)
                intent.putExtra("noHP", transaksi.noHP)
                intent.putExtra("idPelanggan", transaksi.idPelanggan)
                intent.putExtra("namaLayanan", transaksi.namaLayanan)
                intent.putExtra("hargaLayanan", transaksi.hargaLayanan)
                intent.putExtra("idLayanan", transaksi.idLayanan)
                intent.putExtra("idCabang", transaksi.idCabang)
                intent.putExtra("namaCabang", transaksi.namaCabang)
                intent.putExtra("idPegawai", transaksi.idPegawai)
                intent.putExtra("namaPegawai", transaksi.namaPegawai)
                intent.putExtra("totalHarga", transaksi.totalHarga)
                intent.putExtra("layananTambahan", transaksi.layananTambahan)
                startActivity(intent)
                finish() // Close this activity
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.e("KonfirmasiDataActivity", "Error saving transaction: ${e.message}")
                Toast.makeText(
                    this,
                    "Gagal menyimpan transaksi: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
