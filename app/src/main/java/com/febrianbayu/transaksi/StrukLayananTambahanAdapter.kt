package com.febrianbayu.transaksi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.febrianbayu.laundry.R
import com.febrianbayu.modeldata.model_tambahan
import java.text.NumberFormat
import java.util.Locale

class StrukLayananTambahanAdapter(private val items: ArrayList<model_tambahan>) :
    RecyclerView.Adapter<StrukLayananTambahanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIndex: TextView = view.findViewById(R.id.tvIndex)
        val tvNamaTambahan: TextView = view.findViewById(R.id.tvNamaTambahan)
        val tvHargaTambahan: TextView = view.findViewById(R.id.tvHargaTambahan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_struk_layanan_tambahan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tambahan = items[position]

        // Set data to views
        holder.tvIndex.text = "[${position + 1}]"
        holder.tvNamaTambahan.text = tambahan.namaTambahan
        holder.tvHargaTambahan.text = formatRupiah(tambahan.hargaTambahan.toString())
    }

    override fun getItemCount() = items.size

    private fun formatRupiah(amount: String): String {
        try {
            val price = amount.replace("Rp", "").replace(".", "").replace(",00", "").trim().toDouble()
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            return formatRupiah.format(price)
        } catch (e: Exception) {
            return "Rp$amount"
        }
    }
}
