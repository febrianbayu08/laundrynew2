package com.febrianbayu.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.febrianbayu.laundry.R
import com.febrianbayu.modeldata.model_cabang

class AdapterPilihCabang(private val listCabang: ArrayList<model_cabang>) :
    RecyclerView.Adapter<AdapterPilihCabang.ViewHolder>() {

    private lateinit var appContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_pilih_cabang, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listCabang[position]
        holder.tvNama.text = item.namaCabang
        holder.tvAlamat.text = item.alamatCabang

        holder.cvCARD.setOnClickListener {
            val intent = Intent()
            intent.putExtra("idCabang", item.idCabang)
            intent.putExtra("namaCabang", item.namaCabang)
            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }
    }

    override fun getItemCount(): Int = listCabang.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvCARD_PILIHCABANG_nama)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvCARD_PILIHCABANG_alamat)
        val cvCARD: CardView = itemView.findViewById(R.id.cvCARD_PILIHCABANG)
    }
}
