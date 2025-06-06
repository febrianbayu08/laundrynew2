package com.febrianbayu.cabang

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.febrianbayu.adapter.AdapterPilihCabang
import com.febrianbayu.laundry.R
import com.febrianbayu.modeldata.model_cabang
import com.google.firebase.database.*
import java.util.Locale

class PilihCabangActivity : AppCompatActivity() {
    private lateinit var rvPilihCabang: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var tvKosong: TextView
    private lateinit var cabangList: ArrayList<model_cabang>
    private lateinit var filteredList: ArrayList<model_cabang>
    private lateinit var adapter: AdapterPilihCabang
    private val myRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("cabang")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_cabang)

        rvPilihCabang = findViewById(R.id.rvPilihCabang)
        searchView = findViewById(R.id.searchViewCabang)
        tvKosong = findViewById(R.id.tvKosongCabang)

        rvPilihCabang.layoutManager = LinearLayoutManager(this)
        cabangList = ArrayList()
        filteredList = ArrayList()
        adapter = AdapterPilihCabang(filteredList)
        rvPilihCabang.adapter = adapter

        getData()
        setupSearchView()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })
    }

    private fun filter(text: String?) {
        filteredList.clear()
        if (text.isNullOrEmpty()) {
            filteredList.addAll(cabangList)
        } else {
            val query = text.lowercase(Locale.getDefault())
            for (item in cabangList) {
                if (item.namaCabang?.lowercase(Locale.getDefault())?.contains(query) == true) {
                    filteredList.add(item)
                }
            }
        }
        tvKosong.visibility = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
        adapter.notifyDataSetChanged()
    }

    private fun getData() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cabangList.clear()
                if (snapshot.exists()) {
                    for (dataSnap in snapshot.children) {
                        val cabang = dataSnap.getValue(model_cabang::class.java)
                        cabang?.let { cabangList.add(it) }
                    }
                }
                filter(searchView.query.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PilihCabangActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
