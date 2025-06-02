package com.febrianbayu.transaksi

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.febrianbayu.laundry.R
import com.febrianbayu.modeldata.model_tambahan

class LayananTambahanAdapter(private val dataList: List<model_tambahan>) :
    RecyclerView.Adapter<LayananTambahanAdapter.ViewHolder>() {

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
            val item = dataList[position]
            // Log for debugging
            Log.d("LayananTambahanAdapter", "Item at position $position: ${item.namaTambahan}, ${item.hargaTambahan}")

            // Set values with safe handling of nulls
            holder.tvIdTambahan.text = "[$nomor]"
            holder.tvNamaLayananTambahan.text = "Nama Layanan : " + item.namaTambahan.toString()
            holder.tvHargaLayananTambahan.text = "Rp. ${item.hargaTambahan.toString()}"
        } catch (e: Exception) {
            Log.e("LayananTambahanAdapter", "Error binding data at position $position", e)
            holder.tvNamaLayananTambahan.text = "Error data"
            holder.tvHargaLayananTambahan.text = "Rp. 0"
        }
    }

    override fun getItemCount(): Int {
        Log.d("LayananTambahanAdapter", "Data list size: ${dataList.size}")
        return dataList.size
    }
}
