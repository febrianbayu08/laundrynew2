package com.febrianbayu.transaksi

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
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

class DataTransaksiActivity : AppCompatActivity() {
    private lateinit var tvNAMA_PELANGGAN: TextView
    private lateinit var tvNO_HP: TextView
    private lateinit var tvNAMA_LAYANAN: TextView
    private lateinit var tvHARGA_LAYANAN: TextView
    private lateinit var rvLAYANAN_TAMBAHAN: RecyclerView
    private lateinit var btPILIH_PELANGGAN: Button
    private lateinit var btPILIH_LAYANAN: Button
    private lateinit var btPROSES: Button
    private lateinit var btTAMBAHAN: Button
    private val dataList = mutableListOf<model_tambahan>()
    private lateinit var layananTambahanAdapter: LayananTambahanAdapter

    private val pilihPelanggan = 1
    private val pilihLayanan = 2
    private val pilihLayananTambahan = 3

    private var idPelanggan: String = ""
    private var idCabang: String = ""
    private var namaPelanggan: String = ""
    private var noHP: String = ""
    private var idLayanan: String = ""
    private var namaLayanan: String = ""
    private var hargaLayanan: String = ""
    private var idPegawai: String = ""

    private lateinit var sharedPref: SharedPreferences


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_transaksi)

        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        idCabang = sharedPref.getString("idCabang", null).toString()
        idPegawai = sharedPref.getString("idPegawai", null).toString()

        init()

        // Initialize the adapter
        layananTambahanAdapter = LayananTambahanAdapter(dataList)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        rvLAYANAN_TAMBAHAN.layoutManager = layoutManager
        rvLAYANAN_TAMBAHAN.setHasFixedSize(true)
        rvLAYANAN_TAMBAHAN.adapter = layananTambahanAdapter

        btPILIH_PELANGGAN.setOnClickListener {
            val intent = Intent(this, PilihPelangganActivity::class.java)
            startActivityForResult(intent, pilihPelanggan)
        }

        btPILIH_LAYANAN.setOnClickListener {
            val intent = Intent(this, PilihLayananActivity::class.java)
            startActivityForResult(intent, pilihLayanan)
        }

        btTAMBAHAN.setOnClickListener {
            val intent = Intent(this, PilihLayananTambahanActivity::class.java)
            startActivityForResult(intent, pilihLayananTambahan)
        }

        btPROSES.setOnClickListener {
            if (validateInput()) {
                processTransaction()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Validate required inputs are filled
    private fun validateInput(): Boolean {
        if (idPelanggan.isEmpty()) {
            Toast.makeText(this, "Silakan pilih pelanggan terlebih dahulu", Toast.LENGTH_SHORT).show()
            return false
        }
        if (idLayanan.isEmpty()) {
            Toast.makeText(this, "Silakan pilih layanan terlebih dahulu", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // Process transaction data and move to confirmation screen
    private fun processTransaction() {
        try {
            // Calculate total price (main service + additional services)
            val mainServicePrice = hargaLayanan.replace("Rp", "").replace(".", "").replace(",00", "").trim().toInt()
            var totalAdditionalPrice = 0

            for (item in dataList) {
                val additionalPrice = item.hargaTambahan?.replace("Rp", "")?.replace(".", "")?.replace(",00", "")?.trim()?.toInt() ?: 0
                totalAdditionalPrice += additionalPrice
            }

            val totalPrice = mainServicePrice + totalAdditionalPrice

            // Create intent to KonfirmasiDataActivity
            val intent = Intent(this, KonfirmasiDataActivity::class.java)

            // Pass customer data
            intent.putExtra("namaPelanggan", namaPelanggan)
            intent.putExtra("noHP", noHP)
            intent.putExtra("idPelanggan", idPelanggan)

            // Pass main service data]
            intent.putExtra("namaLayanan", namaLayanan)
            intent.putExtra("hargaLayanan", hargaLayanan)
            intent.putExtra("idLayanan", idLayanan)

            // Pass branch and employee IDs
            intent.putExtra("idCabang", idCabang)
            intent.putExtra("idPegawai", idPegawai)

            // Pass total price
            intent.putExtra("totalHarga", totalPrice.toString())

            // Pass additional services as ArrayList
            val tambahanList = ArrayList<model_tambahan>()
            tambahanList.addAll(dataList)
            intent.putExtra("layananTambahan", tambahanList)

            startActivity(intent)
        } catch (e: Exception) {
            Log.e("DataTransaksiActivity", "Error processing transaction: ${e.message}")
            Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun init() {
        tvNAMA_PELANGGAN = findViewById(R.id.tvNamaPelanggan)
        tvNO_HP = findViewById(R.id.tvPelangganNoHP)
        tvNAMA_LAYANAN = findViewById(R.id.tvNamaLayanan)
        tvHARGA_LAYANAN = findViewById(R.id.tvLayananHarga)
        rvLAYANAN_TAMBAHAN = findViewById(R.id.rvLayananTambahan)
        btPILIH_PELANGGAN = findViewById(R.id.btnPilihPelanggan)
        btPILIH_LAYANAN = findViewById(R.id.btnPilihLayanan)
        btTAMBAHAN = findViewById(R.id.btnTambahan)
        btPROSES = findViewById(R.id.btnProses)
    }


    @Deprecated("This method has been deprecated in favor of using the Activity Result API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pilihPelanggan) {
            if (resultCode == RESULT_OK && data != null) {
                idPelanggan = data.getStringExtra("idPelanggan").toString()
                val nama = data.getStringExtra("nama")
                val nomorHP = data.getStringExtra("noHP")

                tvNAMA_PELANGGAN.text = "Nama Pelanggan : $nama"
                tvNO_HP.text = "No HP : $nomorHP"

                namaPelanggan = nama.toString()
                noHP = nomorHP.toString()
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Batal Memilih Pelanggan", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == pilihLayanan){
            if(resultCode == RESULT_OK && data != null){
                idLayanan = data.getStringExtra("idLayanan").toString()
                val nama = data.getStringExtra("nama")
                val harga = data.getStringExtra("harga")

                tvNAMA_LAYANAN.text = "Nama Layanan  : $nama"
                tvHARGA_LAYANAN.text = "Harga : $harga"

                namaLayanan = nama.toString()
                hargaLayanan = harga.toString()
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Batal Memilih Layanan", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == pilihLayananTambahan){
            if(resultCode == RESULT_OK && data != null){
                val idLayananTambahan = data.getStringExtra("idTambahan")
                val namaLayananTambahan = data.getStringExtra("nama")
                val hargaLayananTambahan = data.getStringExtra("harga")
                val idCabang = data.getStringExtra("cabang")

                // Log the received data for debugging
                Log.d("DataTransaksiActivity", "Received: id=$idLayananTambahan, name=$namaLayananTambahan, price=$hargaLayananTambahan")

                // Create model for the selected additional service - match field names with model_tambahan
                val layananTambahan = model_tambahan(
                    idTambahan = idLayananTambahan,
                    namaTambahan = namaLayananTambahan,
                    hargaTambahan = hargaLayananTambahan,
                    cabang = idCabang // Add the cabang ID if needed
                )

                // Add to the list and notify the adapter
                dataList.add(layananTambahan)
                layananTambahanAdapter.notifyDataSetChanged()

                Toast.makeText(this, "Layanan tambahan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Batal Memilih Layanan Tambahan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
