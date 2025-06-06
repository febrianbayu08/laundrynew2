package com.febrianbayu.pegawai
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
import com.febrianbayu.modeldata.model_pegawai

class TambahPegawaiActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pegawai")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etAlamat: EditText
    lateinit var etNoHP: EditText
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var tvCabangTerpilih: TextView
    lateinit var btnPilihCabang: Button
    lateinit var btSimpan: Button

    var pegawaiId: String? = null
    private var selectedCabangId: String? = null
    private val PICK_CABANG_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_pegawai)
        init()

        val dataIntent = intent
        pegawaiId = dataIntent.getStringExtra("idPegawai")
        if (!pegawaiId.isNullOrEmpty()) {
            tvJudul.text = "Edit Data Pegawai"
            btSimpan.text = "Update"
            etNama.setText(dataIntent.getStringExtra("namaPegawai"))
            etAlamat.setText(dataIntent.getStringExtra("alamatPegawai"))
            etNoHP.setText(dataIntent.getStringExtra("noHPPegawai"))
            etEmail.setText(dataIntent.getStringExtra("emailPegawai"))
            etPassword.setText(dataIntent.getStringExtra("passwordPegawai"))
            selectedCabangId = dataIntent.getStringExtra("idCabang")
            tvCabangTerpilih.text = "ID Cabang: ${selectedCabangId ?: "Belum dipilih"}"
        }

        btSimpan.setOnClickListener {
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
        tvJudul = findViewById(R.id.tvpegawaijudul)
        etNama = findViewById(R.id.etpegawainama)
        etAlamat = findViewById(R.id.etpegawaialamat)
        etNoHP = findViewById(R.id.etpegawainohp)
        etEmail = findViewById(R.id.etpegawaiemail)
        etPassword = findViewById(R.id.etpegawaipassword)
        tvCabangTerpilih = findViewById(R.id.tv_cabang_terpilih)
        btnPilihCabang = findViewById(R.id.btn_pilih_cabang)
        btSimpan = findViewById(R.id.btpegawaisimpan)
    }

    fun cekValidasi() {
        val nama = etNama.text.toString()
        val alamat = etAlamat.text.toString()
        val noHp = etNoHP.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (nama.isEmpty() || alamat.isEmpty() || noHp.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCabangId.isNullOrEmpty()) {
            Toast.makeText(this, "Silakan pilih cabang terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        if (pegawaiId.isNullOrEmpty()) {
            simpan()
        } else {
            update()
        }
    }

    fun simpan() {
        val pegawaiBaru = myRef.push()
        val newPegawaiId = pegawaiBaru.key
        val data = model_pegawai(
            idPegawai = newPegawaiId,
            namaPegawai = etNama.text.toString(),
            alamatPegawai = etAlamat.text.toString(),
            noHPPegawai = etNoHP.text.toString(),
            idCabang = selectedCabangId,
            terdaftar = null,
            email = etEmail.text.toString(),
            password = etPassword.text.toString()
        )
        pegawaiBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.tambah_pegawai_sukses), Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.tambah_pegawai_gagal), Toast.LENGTH_SHORT).show()
            }
    }

    fun update() {
        val dataUpdate = mapOf(
            "namaPegawai" to etNama.text.toString(),
            "alamatPegawai" to etAlamat.text.toString(),
            "noHPPegawai" to etNoHP.text.toString(),
            "email" to etEmail.text.toString(),
            "password" to etPassword.text.toString(),
            "idCabang" to selectedCabangId
        )

        myRef.child(pegawaiId ?: "").updateChildren(dataUpdate)
            .addOnSuccessListener {
                Toast.makeText(this, "Berhasil update data pegawai", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal update data pegawai", Toast.LENGTH_SHORT).show()
            }
    }
}
