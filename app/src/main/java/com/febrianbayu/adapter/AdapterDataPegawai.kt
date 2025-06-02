package com.febrianbayu.adapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import com.febrianbayu.laundry.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.febrianbayu.modeldata.model_pegawai
import com.febrianbayu.pelanggan.TambahPelangganActivity

class AdapterDataPegawai(
    private val listPegawai: ArrayList<model_pegawai>) :
    RecyclerView.Adapter<AdapterDataPegawai.ViewHolder>() {
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pegawai, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    @SuppressLint("MissingInflatedId")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listPegawai[position]
        holder.tvCARD_PEGAWAI_ID.text = item.idPegawai
        holder.tvCARD_PEGAWAI_NAMA.text = item.namaPegawai
        holder.tvCARD_PEGAWAI_ALAMAT.text = item.alamatPegawai
        holder.tvCARD_PEGAWAI_NOHP.text = item.noHPPegawai
        holder.tvCARD_PEGAWAI_cabang.text = item.idCabang

        // Klik tombol lihat
        holder.btn_lihat.setOnClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.dialogmod_pegawai, null)

            val dialogBuilder = AlertDialog.Builder(holder.itemView.context)
                .setView(dialogView)
                .setCancelable(true)

            val alertDialog = dialogBuilder.create()

            // Temukan semua TextView di dialog
            val tvId = dialogView.findViewById<TextView>(R.id.tvDIALOG_PEGAWAI_ID)
            val tvNama = dialogView.findViewById<TextView>(R.id.tvDIALOG_PEGAWAI_NAMA)
            val tvAlamat = dialogView.findViewById<TextView>(R.id.tvDIALOG_PEGAWAI_ALAMAT)
            val tvNoHP = dialogView.findViewById<TextView>(R.id.tvDIALOG_PEGAWAI_NOHP)
            val tvCabang = dialogView.findViewById<TextView>(R.id.tvDIALOG_PEGAWAICABANG)

            val btEdit = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_PEGAWAI_Edit)
            val btHapus = dialogView.findViewById<Button>(R.id.btDIALOG_MOD_PEGAWAI_Hapus)

            // Cek null sebelum setText
            tvId?.text = item.idPegawai
            tvNama?.text = item.namaPegawai
            tvAlamat?.text = item.alamatPegawai
            tvNoHP?.text = item.noHPPegawai
            tvCabang?.text = item.idCabang // opsional

            btEdit?.setOnClickListener {
                val intent = Intent(holder.itemView.context, TambahPelangganActivity::class.java)
                intent.putExtra("idPelanggan", item.idPegawai)
                intent.putExtra("namaPelanggan", item.namaPegawai)
                intent.putExtra("alamatPelanggan", item.alamatPegawai)
                intent.putExtra("noHpPelanggan", item.noHPPegawai)
                intent.putExtra("idCabang", item.idCabang)
                holder.itemView.context.startActivity(intent)
                alertDialog.dismiss()
            }

            btHapus?.setOnClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Konfirmasi Hapus")
                    .setMessage("Yakin ingin menghapus data ini?")
                    .setPositiveButton("Ya") { _, _ ->
                        val dbRef = FirebaseDatabase.getInstance()
                            .getReference("Pelanggan")
                            .child(item.idPegawai ?: "")

                        dbRef.removeValue().addOnSuccessListener {
                            listPegawai.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, listPegawai.size)
                            Toast.makeText(holder.itemView.context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                            alertDialog.dismiss()
                        }.addOnFailureListener {
                            Toast.makeText(holder.itemView.context, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return listPegawai.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btn_lihat = itemView.findViewById<View>(R.id.btn_lihat)
        val tvCARD_PEGAWAI_ID = itemView.findViewById<TextView>(R.id.tvCARD_PEGAWAI_ID)
        val tvCARD_PEGAWAI_NAMA = itemView.findViewById<TextView>(R.id.tvCARD_PEGAWAI_nama)
        val tvCARD_PEGAWAI_ALAMAT = itemView.findViewById<TextView>(R.id.tvCARD_PEGAWAI_alamat)
        val tvCARD_PEGAWAI_NOHP = itemView.findViewById<TextView>(R.id.tvCARD_PEGAWAI_nohp)
        val tvCARD_PEGAWAI_cabang = itemView.findViewById<TextView>(R.id.tvCARD_PEGAWAI_cabang)
    }
}