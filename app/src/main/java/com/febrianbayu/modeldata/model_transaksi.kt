package com.febrianbayu.modeldata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class model_transaksi(
    val idTransaksi: String = "",
    val idPelanggan: String = "",
    val namaPelanggan: String = "",
    val noHP: String = "",
    val idLayanan: String = "",
    val namaLayanan: String = "",
    val hargaLayanan: String = "",
    val idCabang: String = "",
    val namaCabang: String = "",
    val idPegawai: String = "",
    val namaPegawai: String = "",
    val tanggalTransaksi: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
    val tanggalAmbil: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
    val statusPembayaran: String = "BELUM_DIBAYAR", // BELUM_DIBAYAR, LUNAS
    val statusPesanan: String = "DIPROSES", // DIPROSES, SELESAI, DIAMBIL
    val metodePembayaran: String = "", // TUNAI, QRIS, DANA, GOPAY, OVO, BAYAR_NANTI
    var layananTambahan: ArrayList<model_tambahan> = ArrayList(),
    val totalHarga: String = "0"
) : Parcelable
