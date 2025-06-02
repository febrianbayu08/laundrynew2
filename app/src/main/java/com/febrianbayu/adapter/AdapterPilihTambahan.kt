package com.febrianbayu.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.febrianbayu.laundry.R
import com.febrianbayu.modeldata.model_tambahan
import com.febrianbayu.transaksi.DataTransaksiActivity

class AdapterPilihTambahan(private val listLayanan: ArrayList<model_tambahan>) :
    RecyclerView.Adapter<AdapterPilihTambahan.ViewHolder>() {

    private val TAG = "AdapterPilihTambahan"
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        Log.d(TAG, "onCreateViewHolder called")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_pilih_tambahan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder for position: $position")
        val nomor = position + 1
        val item = listLayanan[position]

        try {
            holder.tvID.text = "[$nomor]"
            holder.tvNama.text = item.namaTambahan ?: "Nama tidak tersedia"
            holder.tvHarga.text = "Harga : Rp ${item.hargaTambahan ?: "Tidak tersedia"}"

            Log.d(TAG, "Binding data: ${item.namaTambahan} at position $position")

            holder.cvCARD.setOnClickListener {
                try {
                    val intent = Intent(appContext, DataTransaksiActivity::class.java)
                    intent.putExtra("idTambahan", item.idTambahan)
                    intent.putExtra("nama", item.namaTambahan)
                    intent.putExtra("harga", item.hargaTambahan)
                    intent.putExtra("cabang", item.cabang)
                    (appContext as Activity).setResult(Activity.RESULT_OK, intent)
                    (appContext as Activity).finish()
                } catch (e: Exception) {
                    Log.e(TAG, "Error in click listener: ${e.message}")
                    Toast.makeText(appContext, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error binding view holder: ${e.message}")
        }
    }

    override fun getItemCount(): Int {
        val count = listLayanan.size
        Log.d(TAG, "getItemCount: $count")
        return count
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvID: TextView = itemView.findViewById(R.id.tvId)
        val tvNama: TextView = itemView.findViewById(R.id.tvNamaTambahan)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHargaTambahan)
        val cvCARD: CardView = itemView.findViewById(R.id.cvCARD_PILIHTAMBAHAN)
    }
}
