package com.febrianbayu.transaksi

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.febrianbayu.laundry.R
import com.febrianbayu.modeldata.model_tambahan

class KonfirmasiLayananTambahanAdapter(
    private val arrayList: ArrayList<model_tambahan>
) : RecyclerView.Adapter<KonfirmasiLayananTambahanAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdTambahan: TextView = itemView.findViewById(R.id.tvCARD_TAMBAHAN_ID)
        val tvNamaLayananTambahan: TextView = itemView.findViewById(R.id.tvNamaLayananTambahan)
        val tvHargaLayananTambahan: TextView = itemView.findViewById(R.id.tvHargaLayananTambahan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layanan_tambahan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val nomor = position + 1
            val item = arrayList[position]

            Log.d("KonfirmasiAdapter", "Item ke-$nomor: ${item.namaTambahan}, ${item.hargaTambahan}")

            holder.tvIdTambahan.text = "[$nomor]"
            holder.tvNamaLayananTambahan.text = "Nama Layanan : ${item.namaTambahan ?: "-"}"
            holder.tvHargaLayananTambahan.text = "Rp. ${item.hargaTambahan ?: "0"}"
        } catch (e: Exception) {
            Log.e("KonfirmasiAdapter", "Error binding item di posisi $position", e)
            holder.tvIdTambahan.text = "[$position]"
            holder.tvNamaLayananTambahan.text = "Error"
            holder.tvHargaLayananTambahan.text = "Rp. 0"
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}
