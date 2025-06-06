package com.febrianbayu.laporan

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.febrianbayu.laundry.R
import com.febrianbayu.modeldata.model_transaksi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DataLaporanActivity : AppCompatActivity() {

    private lateinit var rvDataLaporan: RecyclerView
    private lateinit var tvLaporanKosong: TextView
    private lateinit var laporanList: ArrayList<model_transaksi>
    private lateinit var adapter: AdapterDataLaporan

    private val database = FirebaseDatabase.getInstance().getReference("transaksi")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Use the new layout file
        setContentView(R.layout.activity_data_laporan)

        rvDataLaporan = findViewById(R.id.rv_data_laporan)
        tvLaporanKosong = findViewById(R.id.tv_laporan_kosong)

        rvDataLaporan.layoutManager = LinearLayoutManager(this)
        rvDataLaporan.setHasFixedSize(true)

        laporanList = ArrayList()
        adapter = AdapterDataLaporan(this, laporanList)
        rvDataLaporan.adapter = adapter

        fetchDataLaporan()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchDataLaporan() {
        tvLaporanKosong.visibility = View.GONE
        rvDataLaporan.visibility = View.VISIBLE

        val query = database.orderByKey().limitToLast(100)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                laporanList.clear()
                if (snapshot.exists()) {
                    for (transaksiSnapshot in snapshot.children) {
                        val transaksi = transaksiSnapshot.getValue(model_transaksi::class.java)
                        transaksi?.let { laporanList.add(it) }
                    }
                    laporanList.reverse() // Show the newest transactions first
                    adapter.notifyDataSetChanged()
                }

                if (laporanList.isEmpty()) {
                    tvLaporanKosong.visibility = View.VISIBLE
                    rvDataLaporan.visibility = View.GONE
                } else {
                    tvLaporanKosong.visibility = View.GONE
                    rvDataLaporan.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataLaporanActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
                tvLaporanKosong.visibility = View.VISIBLE
                rvDataLaporan.visibility = View.GONE
            }
        })
    }
}
