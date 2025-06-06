package com.febrianbayu.laporan

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.febrianbayu.laundry.R
import com.febrianbayu.modeldata.model_transaksi
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdapterDataLaporan(
    private val context: Context,
    private val listLaporan: ArrayList<model_transaksi>
) : RecyclerView.Adapter<AdapterDataLaporan.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.card_laporan_transaksi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listLaporan[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = listLaporan.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNamaPelanggan: TextView = itemView.findViewById(R.id.tv_laporan_nama_pelanggan)
        private val tvIdTransaksi: TextView = itemView.findViewById(R.id.tv_laporan_id_transaksi)
        private val tvTanggal: TextView = itemView.findViewById(R.id.tv_laporan_tanggal)
        private val tvStatusPesanan: TextView = itemView.findViewById(R.id.tv_laporan_status_pesanan)
        private val tvStatusPembayaran: TextView = itemView.findViewById(R.id.tv_laporan_status_pembayaran)
        private val tvTotalHarga: TextView = itemView.findViewById(R.id.tv_laporan_total_harga)

        fun bind(transaksi: model_transaksi) {
            tvNamaPelanggan.text = transaksi.namaPelanggan
            tvIdTransaksi.text = "ID: ${transaksi.idTransaksi}"
            tvTanggal.text = "Tanggal: ${transaksi.tanggalTransaksi}"
            tvTotalHarga.text = formatRupiah(transaksi.totalHarga)

            // Set Status Pesanan
            tvStatusPesanan.text = transaksi.statusPesanan
            when (transaksi.statusPesanan) {
                "DIPROSES" -> tvStatusPesanan.setBackgroundColor(Color.parseColor("#FFA500")) // Orange
                "SELESAI" -> tvStatusPesanan.setBackgroundColor(Color.parseColor("#3498DB"))  // Blue
                "DIAMBIL" -> tvStatusPesanan.setBackgroundColor(Color.parseColor("#2ECC71"))  // Green
                else -> tvStatusPesanan.setBackgroundColor(Color.LTGRAY)
            }

            // Set Status Pembayaran
            tvStatusPembayaran.text = transaksi.statusPembayaran.replace("_", " ")
            if (transaksi.statusPembayaran == "LUNAS") {
                tvStatusPembayaran.setTextColor(Color.parseColor("#27AE60")) // Green
            } else {
                tvStatusPembayaran.setTextColor(Color.parseColor("#E74C3C")) // Red
            }

            itemView.setOnClickListener {
                showEditDialog(transaksi)
            }
        }

        private fun showEditDialog(transaksi: model_transaksi) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_laporan, null)
            val builder = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)

            val spinnerStatusPesanan: Spinner = dialogView.findViewById(R.id.spinner_status_pesanan)
            val spinnerStatusPembayaran: Spinner = dialogView.findViewById(R.id.spinner_status_pembayaran)
            val etTanggalAmbil: EditText = dialogView.findViewById(R.id.et_tanggal_ambil)
            val btnUpdate: Button = dialogView.findViewById(R.id.btn_update_laporan)
            val btnBatal: Button = dialogView.findViewById(R.id.btn_batal_edit)

            val dialog = builder.create()

            // Setup Spinners
            val statusPesananOptions = arrayOf("DIPROSES", "SELESAI", "DIAMBIL")
            val statusPembayaranOptions = arrayOf("BELUM_DIBAYAR", "LUNAS")

            spinnerStatusPesanan.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, statusPesananOptions)
            spinnerStatusPembayaran.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, statusPembayaranOptions)

            // Set initial spinner selections
            spinnerStatusPesanan.setSelection(statusPesananOptions.indexOf(transaksi.statusPesanan))
            spinnerStatusPembayaran.setSelection(statusPembayaranOptions.indexOf(transaksi.statusPembayaran))
            etTanggalAmbil.setText(transaksi.tanggalAmbil)

            spinnerStatusPesanan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (statusPesananOptions[position] == "DIAMBIL") {
                        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                        etTanggalAmbil.setText(currentDate)
                    } else {
                        etTanggalAmbil.setText("")
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            btnUpdate.setOnClickListener {
                val newStatusPesanan = spinnerStatusPesanan.selectedItem.toString()
                val newStatusPembayaran = spinnerStatusPembayaran.selectedItem.toString()
                val newTanggalAmbil = etTanggalAmbil.text.toString()

                updateTransactionInFirebase(transaksi.idTransaksi, newStatusPesanan, newStatusPembayaran, newTanggalAmbil, dialog)
            }

            btnBatal.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        private fun updateTransactionInFirebase(
            idTransaksi: String,
            statusPesanan: String,
            statusPembayaran: String,
            tanggalAmbil: String,
            dialog: AlertDialog
        ) {
            val transactionRef = FirebaseDatabase.getInstance().getReference("transaksi").child(idTransaksi)
            val updates = mapOf(
                "statusPesanan" to statusPesanan,
                "statusPembayaran" to statusPembayaran,
                "tanggalAmbil" to tanggalAmbil
            )

            transactionRef.updateChildren(updates)
                .addOnSuccessListener {
                    Toast.makeText(context, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Gagal memperbarui data: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun formatRupiah(amount: String): String {
        return try {
            val cleanAmount = amount.replace(Regex("[^0-9]"), "")
            if (cleanAmount.isEmpty()) return "Rp0"
            val price = cleanAmount.toDouble()
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            formatRupiah.maximumFractionDigits = 0
            formatRupiah.format(price)
        } catch (e: Exception) {
            "Rp$amount"
        }
    }
}
