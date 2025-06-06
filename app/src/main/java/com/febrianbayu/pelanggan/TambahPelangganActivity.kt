package com.febrianbayu.pelanggan
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.febrianbayu.cabang.PilihCabangActivity
import com.google.firebase.database.FirebaseDatabase
import com.febrianbayu.laundry.R
import com.febrianbayu.modeldata.model_pelanggan

class TambahPelangganActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pelanggan")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etAlamat: EditText
    lateinit var etNoHP: EditText
    lateinit var tvCabangTerpilih: TextView
    lateinit var btnPilihCabang: Button
    lateinit var btSimpan: Button

    var pelangganId: String? = null
    private var selectedCabangId: String? = null
    private val PICK_CABANG_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_pelanggan)
        init()
        val dataIntent = intent
        if (dataIntent != null) {
            pelangganId = dataIntent.getStringExtra("idPelanggan")
            val nama = dataIntent.getStringExtra("namaPelanggan")
            val alamat = dataIntent.getStringExtra("alamatPelanggan")
            val noHp = dataIntent.getStringExtra("noHpPelanggan")
            selectedCabangId = dataIntent.getStringExtra("idCabang")
            tvCabangTerpilih.text = "ID Cabang: ${selectedCabangId ?: "Belum dipilih"}"


            if (pelangganId != null) {
                tvJudul.text = "Edit Data Pelanggan"
                btSimpan.text = "Update"
                etNama.setText(nama)
                etAlamat.setText(alamat)
                etNoHP.setText(noHp)
                tvCabangTerpilih.setText(selectedCabangId)
            }
        }
        btSimpan.setOnClickListener{
            cekValidasi()
        }
        btnPilihCabang.setOnClickListener {
            val intent = Intent(this, PilihCabangActivity::class.java)
            startActivityForResult(intent, PICK_CABANG_REQUEST)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CABANG_REQUEST && resultCode == RESULT_OK) {
            selectedCabangId = data?.getStringExtra("idCabang")
            val namaCabang = data?.getStringExtra("namaCabang")
            tvCabangTerpilih.text = namaCabang ?: "Gagal memuat nama cabang"
        }
    }
    fun init(){
        tvJudul = findViewById(R.id.tvpelangganjudul)
        etNama = findViewById(R.id.etpelanggannama)
        etAlamat = findViewById(R.id.etpelangganalamat)
        etNoHP = findViewById(R.id.etpelanggannohp)
        tvCabangTerpilih = findViewById(R.id.tv_cabang_terpilih)
        btnPilihCabang = findViewById(R.id.btn_pilih_cabang)
        btSimpan = findViewById(R.id.btpelanggansimpan)
    }

    fun cekValidasi() {
        val nama = etNama.text.toString()
        val alamat = etAlamat.text.toString()
        val noHp = etNoHP.text.toString()

        if (nama.isEmpty()) {
            etNama.error = this.getString(R.string.validasi_nama_pelanggan)
            Toast.makeText(this, this.getString(R.string.validasi_nama_pelanggan),Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamat.error = this.getString(R.string.validasi_alamat_pelanggan)
            Toast.makeText(this, this.getString(R.string.validasi_alamat_pelanggan),Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return
        }
        if (noHp.isEmpty()) {
            etNoHP.error = this.getString(R.string.validasi_nohp_pelanggan)
            Toast.makeText(this, this.getString(R.string.validasi_nohp_pelanggan),Toast.LENGTH_SHORT).show()
            etNoHP.requestFocus()
            return
        }
        if (selectedCabangId.isNullOrEmpty()) {
            Toast.makeText(this, "Silakan pilih cabang terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        if (pelangganId == null) {
            simpan()
        } else {
            update()
        }
    }

    fun simpan() {
        val pelangganBaru = myRef.push()
        val pelangganid = pelangganBaru.key
        val data = model_pelanggan(
            pelangganid.toString(),
            etNama.text.toString(),
            etAlamat.text.toString(),
            etNoHP.text.toString(),
            tvCabangTerpilih.text.toString(),
        )
        pelangganBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                this.getString(R.string.tambah_pelanggan_sukses),
                    Toast.LENGTH_SHORT
                )
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                this.getString(R.string.tambah_pelanggan_gagal),
                    Toast.LENGTH_SHORT
                )
            }
    }
    fun update() {
        val dataUpdate = mapOf(
            "namaPelanggan" to etNama.text.toString(),
            "alamatPelanggan" to etAlamat.text.toString(),
            "noHPPelanggan" to etNoHP.text.toString(),
            "idCabang" to selectedCabangId
        )

        myRef.child(pelangganId ?: "").updateChildren(dataUpdate)
            .addOnSuccessListener {
                Toast.makeText(this, "Berhasil update data pelanggan", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal update data pelanggan", Toast.LENGTH_SHORT).show()
            }
    }
}
