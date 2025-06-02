package com.febrianbayu.transaksi

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.febrianbayu.laundry.MainActivity
import com.febrianbayu.laundry.R
import com.febrianbayu.modeldata.model_tambahan
import com.febrianbayu.modeldata.model_transaksi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import java.io.IOException
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import java.nio.charset.Charset

class TransaksiSelesaiActivity : AppCompatActivity() {

    // ... (Your existing properties: TextViews, Buttons, etc.)
    private lateinit var tvIdTransaksi: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvNamaPelanggan: TextView
    private lateinit var tvNamaPegawai: TextView
    private lateinit var tvNamaLayanan: TextView
    private lateinit var tvHargaLayanan: TextView
    private lateinit var rvLayananTambahan: RecyclerView
    private lateinit var tvSubtotalTambahan: TextView
    private lateinit var tvTotalBayar: TextView
    private lateinit var btnKirimWhatsapp: Button
    private lateinit var btnCetak: Button
    private lateinit var btnSelesai: Button

    private var transaksi: model_transaksi? = null
    private var layananTambahanList = ArrayList<model_tambahan>()
    private lateinit var layananTambahanAdapter: StrukLayananTambahanAdapter
    private lateinit var database: FirebaseDatabase


    // Bluetooth components
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var selectedBluetoothDevice: BluetoothDevice? = null

    private val TARGET_PRINTER_NAME = "RPP02N" // Example printer name, adjust!
    private val PRINTER_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val requestBluetoothPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var allGranted = true
            permissions.entries.forEach {
                if (!it.value) allGranted = false
            }
            if (allGranted) {
                Log.d("BluetoothPermission", "All Bluetooth permissions granted.")
                initBluetoothAndPrint()
            } else {
                Toast.makeText(this, "Izin Bluetooth diperlukan untuk mencetak", Toast.LENGTH_LONG).show()
            }
        }

    private val enableBluetoothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d("BluetoothEnable", "Bluetooth enabled by user.")
                initBluetoothAndPrint()
            } else {
                Toast.makeText(this, "Bluetooth harus diaktifkan untuk mencetak", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaksi_selesai)

        database = FirebaseDatabase.getInstance()
        initViews()
        getIntentData()
        setupRecyclerView()
        setupButtonListeners()

        // Handler for btnSelesai update is now inside data loading functions
    }

    private fun initViews() {
        tvIdTransaksi = findViewById(R.id.tvIdTransaksi)
        tvTanggal = findViewById(R.id.tvTanggal)
        tvNamaPelanggan = findViewById(R.id.tvNamaPelanggan)
        tvNamaPegawai = findViewById(R.id.tvNamaPegawai)
        tvNamaLayanan = findViewById(R.id.tvNamaLayanan)
        tvHargaLayanan = findViewById(R.id.tvHargaLayanan)
        rvLayananTambahan = findViewById(R.id.rvLayananTambahan)
        tvSubtotalTambahan = findViewById(R.id.tvSubtotalTambahan)
        tvTotalBayar = findViewById(R.id.tvTotalBayar)
        btnKirimWhatsapp = findViewById(R.id.btnKirimWhatsapp)
        btnCetak = findViewById(R.id.btnCetak)
        btnSelesai = findViewById(R.id.btnSelesai)
    }

    private fun getIntentData() {
        val idTransaksi = intent.getStringExtra("idTransaksi") ?: ""
        if (idTransaksi.isNotEmpty()) {
            fetchTransactionFromFirebase(idTransaksi)
        } else {
            getDataFromIntentExtras()
        }
    }

    private fun fetchTransactionFromFirebase(idTransaksi: String) {
        val transactionRef = database.reference.child("transaksi").child(idTransaksi)
        transactionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        transaksi = snapshot.getValue(model_transaksi::class.java)
                        val tambahanSnapshot = snapshot.child("layananTambahan")
                        layananTambahanList.clear()
                        if (tambahanSnapshot.exists()) {
                            for (childSnapshot in tambahanSnapshot.children) {
                                val tambahan = childSnapshot.getValue(model_tambahan::class.java)
                                tambahan?.let { layananTambahanList.add(it) }
                            }
                        }
                        transaksi?.layananTambahan = ArrayList(layananTambahanList)
                        displayTransactionData()
                        updateSelesaiButtonState()
                    } catch (e: Exception) {
                        Log.e("FetchFirebase", "Error parsing Firebase data: ${e.message}")
                        getDataFromIntentExtras()
                    }
                } else {
                    Log.w("FetchFirebase", "Transaksi $idTransaksi tidak ditemukan.")
                    getDataFromIntentExtras()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("FetchFirebase", "Database error: ${error.message}")
                getDataFromIntentExtras()
            }
        })
    }

    private fun getDataFromIntentExtras() {
        // ... (Same as your existing getDataFromIntentExtras)
        val idTransaksi = intent.getStringExtra("idTransaksi") ?: ("TRX" + System.currentTimeMillis())
        val metodePembayaran = intent.getStringExtra("metodePembayaran") ?: "TUNAI"
        val namaPelanggan = intent.getStringExtra("namaPelanggan") ?: "N/A"
        val noHP = intent.getStringExtra("noHP") ?: "N/A"
        val idPelanggan = intent.getStringExtra("idPelanggan") ?: "N/A"
        val namaLayanan = intent.getStringExtra("namaLayanan") ?: "N/A"
        val hargaLayanan = intent.getStringExtra("hargaLayanan") ?: "0"
        val idLayanan = intent.getStringExtra("idLayanan") ?: "N/A"
        val idCabang = intent.getStringExtra("idCabang") ?: "N/A"
        val namaCabang = intent.getStringExtra("namaCabang") ?: "Pusat"
        val idPegawai = intent.getStringExtra("idPegawai") ?: "N/A"
        val namaPegawai = intent.getStringExtra("namaPegawai") ?: "Kasir"
        val totalHarga = intent.getStringExtra("totalHarga") ?: "0"

        layananTambahanList.clear()
        if (intent.hasExtra("layananTambahan")) {
            val tambahanFromIntent = intent.getParcelableArrayListExtra<model_tambahan>("layananTambahan")
            tambahanFromIntent?.let { layananTambahanList.addAll(it) }
        }

        transaksi = model_transaksi(
            idTransaksi = idTransaksi,
            idPelanggan = idPelanggan,
            namaPelanggan = namaPelanggan,
            noHP = noHP,
            idLayanan = idLayanan,
            namaLayanan = namaLayanan,
            hargaLayanan = hargaLayanan,
            idCabang = idCabang,
            namaCabang = namaCabang,
            idPegawai = idPegawai,
            namaPegawai = namaPegawai,
            tanggalTransaksi = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            statusPembayaran = if (metodePembayaran.equals("BAYAR_NANTI", ignoreCase = true)) "BELUM_DIBAYAR" else "LUNAS",
            statusPesanan = "DIPROSES",
            metodePembayaran = metodePembayaran,
            layananTambahan = ArrayList(layananTambahanList),
            totalHarga = totalHarga
        )
        displayTransactionData()
        updateSelesaiButtonState()
    }

    private fun updateSelesaiButtonState() {
        Handler(Looper.getMainLooper()).postDelayed({ // Added delay to ensure data might be ready
            if (transaksi != null) {
                btnSelesai.text = "Transaksi Sukses Disimpan"
                btnSelesai.isEnabled = true
            } else {
                btnSelesai.text = "Memproses..."
                btnSelesai.isEnabled = false // Keep it disabled until transaction data is confirmed
                Log.w("UpdateSelesai", "Transaksi masih null saat update tombol selesai.")
            }
        },100) // Short delay, adjust if needed
    }

    private fun setupRecyclerView() {
        layananTambahanAdapter = StrukLayananTambahanAdapter(layananTambahanList)
        rvLayananTambahan.layoutManager = LinearLayoutManager(this)
        rvLayananTambahan.adapter = layananTambahanAdapter
    }

    private fun displayTransactionData() {
        transaksi?.let { trx ->
            tvIdTransaksi.text = trx.idTransaksi
            tvTanggal.text = formatDate(trx.tanggalTransaksi)
            tvNamaPelanggan.text = trx.namaPelanggan
            tvNamaPegawai.text = trx.namaPegawai
            tvNamaLayanan.text = trx.namaLayanan
            tvHargaLayanan.text = formatRupiah(trx.hargaLayanan)
            val subtotalTambahan = calculateSubtotalTambahan(trx.layananTambahan)
            tvSubtotalTambahan.text = formatRupiah(subtotalTambahan.toString())
            tvTotalBayar.text = formatRupiah(trx.totalHarga)
            layananTambahanAdapter.notifyDataSetChanged()
        }
    }

    // ... (formatDate, formatRupiah, formatRupiahForPrint, calculateSubtotalTambahan - unchanged)
    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yy, HH:mm", Locale("in", "ID"))
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
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
    private fun formatRupiahForPrint(amount: String): String {
        return try {
            val cleanAmount = amount.replace(Regex("[^0-9]"), "")
            if (cleanAmount.isEmpty()) return "0"
            val price = cleanAmount.toLong()
            val formatter = NumberFormat.getNumberInstance(Locale.GERMAN)
            formatter.format(price)
        } catch (e: Exception) {
            amount
        }
    }

    private fun calculateSubtotalTambahan(layananTambahan: ArrayList<model_tambahan>?): Int {
        var subtotal = 0
        layananTambahan?.forEach { tambahan ->
            try {
                subtotal += tambahan.hargaTambahan?.replace(Regex("[^0-9]"), "")?.toIntOrNull() ?: 0
            } catch (e: Exception) { /* Log error if necessary */ }
        }
        return subtotal
    }


    private fun setupButtonListeners() {
        btnKirimWhatsapp.setOnClickListener { shareReceiptToWhatsapp() }
        btnCetak.setOnClickListener { checkBluetoothPermissionsAndPrint() }
        btnSelesai.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    // ... (shareReceiptToWhatsapp, buildReceiptMessageForWhatsapp, getReadablePaymentMethod - unchanged)
    private fun shareReceiptToWhatsapp() {
        val receiptMessage = buildReceiptMessageForWhatsapp()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, receiptMessage)
            type = "text/plain"
        }
        try {
            startActivity(Intent.createChooser(sendIntent, "Bagikan Struk via"))
        } catch (e: Exception) {
            Toast.makeText(this, "Tidak dapat membagikan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildReceiptMessageForWhatsapp(): String {
        val receipt = StringBuilder()
        transaksi?.let { trx ->
            receipt.append("*Bayu Laundry ${trx.namaCabang}*\n\n")
            receipt.append("ID Transaksi: ${trx.idTransaksi}\n")
            // ... (rest of the WhatsApp message build)
            receipt.append("Tanggal: ${formatDate(trx.tanggalTransaksi)}\n")
            receipt.append("Pelanggan: ${trx.namaPelanggan}\n")
            receipt.append("Karyawan: ${trx.namaPegawai}\n\n")
            receipt.append("Layanan Utama:\n")
            receipt.append("${trx.namaLayanan}: ${formatRupiah(trx.hargaLayanan)}\n\n")

            if (trx.layananTambahan.isNotEmpty()) {
                receipt.append("*Rincian Tambahan:*\n")
                trx.layananTambahan.forEachIndexed { index, tambahan ->
                    receipt.append("[${index + 1}] ${tambahan.namaTambahan}: ${tambahan.hargaTambahan?.let { formatRupiah(it) }}\n")
                }
                receipt.append("\nSubtotal Tambahan: ${formatRupiah(calculateSubtotalTambahan(trx.layananTambahan).toString())}\n")
            }
            receipt.append("\n*Total Bayar: ${formatRupiah(trx.totalHarga)}*\n\n")
            receipt.append("Status Pembayaran: ${if (trx.statusPembayaran == "LUNAS") "Lunas" else "Belum Dibayar"}\n")
            receipt.append("Metode Pembayaran: ${getReadablePaymentMethod(trx.metodePembayaran)}\n\n")
            receipt.append("Terima kasih telah menggunakan jasa Bayu Laundry!")
        }
        return receipt.toString()
    }
    private fun getReadablePaymentMethod(method: String): String {
        return when (method.uppercase()) {
            "TUNAI" -> "Tunai"
            "QRIS" -> "QRIS"
            "DANA" -> "DANA"
            "GOPAY" -> "GoPay"
            "OVO" -> "OVO"
            "BAYAR_NANTI" -> "Bayar Nanti"
            else -> method
        }
    }

    // --- BLUETOOTH PRINTING LOGIC ---

    private fun checkBluetoothPermissionsAndPrint() {
        Log.d("BluetoothCheck", "Checking Bluetooth permissions.")
        val requiredPermissions: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
        }

        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            requestBluetoothPermissionLauncher.launch(missingPermissions.toTypedArray())
        } else {
            initBluetoothAndPrint()
        }
    }

    private fun initBluetoothAndPrint() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth tidak didukung", Toast.LENGTH_LONG).show()
            return
        }

        // Check for BLUETOOTH_CONNECT permission before calling isEnabled or ACTION_REQUEST_ENABLE
        val connectPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_CONNECT else Manifest.permission.BLUETOOTH
        if (ContextCompat.checkSelfPermission(this, connectPermission) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Izin $connectPermission diperlukan", Toast.LENGTH_SHORT).show()
            Log.w("BluetoothInit", "$connectPermission not granted for isEnabled/requestEnable check.")
            // Optionally, re-trigger permission request here, or guide user.
            // For now, we rely on checkBluetoothPermissionsAndPrint to have handled it.
            return
        }

        try {
            if (!bluetoothAdapter!!.isEnabled) {
                Log.d("BluetoothInit", "Bluetooth not enabled, requesting.")
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBluetoothLauncher.launch(enableBtIntent)
                return // Wait for launcher result
            }
        } catch (e: SecurityException) {
            Log.e("BluetoothInit", "SecurityException checking/enabling Bluetooth: ${e.message}", e)
            Toast.makeText(this, "Kesalahan keamanan Bluetooth.", Toast.LENGTH_LONG).show()
            return
        }

        Log.d("BluetoothInit", "Bluetooth enabled.")
        if (selectedBluetoothDevice != null && (bluetoothSocket == null || bluetoothSocket?.isConnected == false)) {
            connectToDeviceAndPrint(selectedBluetoothDevice!!)
        } else if (bluetoothSocket?.isConnected == true) {
            sendDataToPrinter()
        } else {
            findAndConnectPrinter()
        }
    }

    private fun findAndConnectPrinter() {
        val connectPerm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_CONNECT else Manifest.permission.BLUETOOTH
        // For bondedDevices, BLUETOOTH_CONNECT is needed on S+. For older versions, BLUETOOTH is enough.
        if (ContextCompat.checkSelfPermission(this, connectPerm) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Izin $connectPerm untuk mencari printer.", Toast.LENGTH_LONG).show()
            return
        }

        val pairedDevices: Set<BluetoothDevice>?
        try {
            pairedDevices = bluetoothAdapter?.bondedDevices
        } catch (e: SecurityException) {
            Log.e("FindPrinter", "SecurityException getting bonded devices: ${e.message}", e)
            Toast.makeText(this, "Kesalahan keamanan: ${e.message}", Toast.LENGTH_LONG).show()
            return
        }

        val printerDevices = mutableListOf<BluetoothDevice>()
        val printerDeviceNames = mutableListOf<String>()

        pairedDevices?.forEach { device ->
            try {
                // device.name also requires BLUETOOTH_CONNECT on S+
                val deviceName = device.name ?: "Unknown Device"
                printerDevices.add(device)
                printerDeviceNames.add(deviceName)
            } catch (e: SecurityException) {
                Log.e("FindPrinter", "SecurityException getting name for ${device.address}: ${e.message}")
                printerDevices.add(device) // Add with address if name fails
                printerDeviceNames.add(device.address ?: "No Address")
            }
        }

        if (printerDevices.isEmpty()) {
            Toast.makeText(this, "Tidak ada printer Bluetooth ter-pair.", Toast.LENGTH_LONG).show()
            return
        }

        val targetDevice = printerDevices.find {
            try {
                it.name.equals(TARGET_PRINTER_NAME, ignoreCase = true)
            } catch (e: SecurityException) { false } // If name access fails, it's not our target by name
        }

        if (targetDevice != null) {
            selectedBluetoothDevice = targetDevice
            connectToDeviceAndPrint(selectedBluetoothDevice!!)
        } else {
            AlertDialog.Builder(this)
                .setTitle("Pilih Printer")
                .setItems(printerDeviceNames.toTypedArray()) { _, which ->
                    selectedBluetoothDevice = printerDevices[which]
                    connectToDeviceAndPrint(selectedBluetoothDevice!!)
                }
                .setNegativeButton("Batal", null).show()
        }
    }

    private fun connectToDeviceAndPrint(device: BluetoothDevice) {
        val connectPerm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_CONNECT else Manifest.permission.BLUETOOTH
        val scanPerm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_SCAN else Manifest.permission.BLUETOOTH_ADMIN

        if (ContextCompat.checkSelfPermission(this, connectPerm) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Izin $connectPerm untuk koneksi.", Toast.LENGTH_LONG).show()
            return
        }

        coroutineScope.launch {
            try {
                val deviceName = try { device.name } catch (e: SecurityException) { device.address ?: "Unknown" }
                withContext(Dispatchers.Main) { Toast.makeText(this@TransaksiSelesaiActivity, "Menghubungkan ke $deviceName...", Toast.LENGTH_SHORT).show() }

                closeBluetoothConnection() // Close old one first

                bluetoothSocket = device.createRfcommSocketToServiceRecord(PRINTER_UUID)

                if (ContextCompat.checkSelfPermission(this@TransaksiSelesaiActivity, scanPerm) == PackageManager.PERMISSION_GRANTED) {
                    try { bluetoothAdapter?.cancelDiscovery() }
                    catch (e: SecurityException) { Log.w("Connect", "SecEx on cancelDiscovery: ${e.message}") }
                } else { Log.w("Connect", "$scanPerm not granted for cancelDiscovery.") }

                bluetoothSocket?.connect()
                outputStream = bluetoothSocket?.outputStream

                withContext(Dispatchers.Main) { Toast.makeText(this@TransaksiSelesaiActivity, "Terhubung ke $deviceName", Toast.LENGTH_SHORT).show() }
                sendDataToPrinter()

            } catch (e: IOException) {
                val deviceNameInfo = try { device.name } catch (se: SecurityException) { device.address ?: "Unknown" }
                Log.e("Connect", "Koneksi ke $deviceNameInfo gagal: ${e.message}", e)
                withContext(Dispatchers.Main) { Toast.makeText(this@TransaksiSelesaiActivity, "Koneksi gagal: ${e.localizedMessage}", Toast.LENGTH_LONG).show() }
                closeBluetoothConnection()
            } catch (e: SecurityException) {
                Log.e("Connect", "SecurityException: ${e.message}", e)
                withContext(Dispatchers.Main) { Toast.makeText(this@TransaksiSelesaiActivity, "Kesalahan keamanan: ${e.localizedMessage}", Toast.LENGTH_LONG).show() }
                closeBluetoothConnection()
            }
        }
    }

    private fun sendDataToPrinter() {
        if (outputStream == null || bluetoothSocket?.isConnected != true) {
            Toast.makeText(this, "Tidak terhubung ke printer.", Toast.LENGTH_SHORT).show()
            selectedBluetoothDevice?.let { connectToDeviceAndPrint(it) } ?: findAndConnectPrinter()
            return
        }
        coroutineScope.launch {
            try {
                val receiptData = buildPrintableReceiptString()
                if (receiptData.isEmpty()) {
                    withContext(Dispatchers.Main) { Toast.makeText(this@TransaksiSelesaiActivity, "Tidak ada data untuk dicetak", Toast.LENGTH_SHORT).show() }
                    return@launch
                }
                val charSetToUse = Charset.forName("CP437")
                outputStream?.write(receiptData.toByteArray(charSetToUse))
                outputStream?.write(byteArrayOf(0x0A, 0x0A, 0x0A)) // Feed
                outputStream?.write(byteArrayOf(0x1D, 0x56, 0x01)) // Cut
                outputStream?.flush()
                withContext(Dispatchers.Main) { Toast.makeText(this@TransaksiSelesaiActivity, "Struk dikirim ke printer", Toast.LENGTH_SHORT).show() }
            } catch (e: IOException) {
                Log.e("SendData", "Gagal mengirim data: ${e.message}", e)
                withContext(Dispatchers.Main) { Toast.makeText(this@TransaksiSelesaiActivity, "Gagal mengirim data: ${e.message}", Toast.LENGTH_LONG).show() }
            }
        }
    }

    // ... (buildPrintableReceiptString - unchanged from previous corrected version)
    private fun buildPrintableReceiptString(): String {
        if (transaksi == null) return ""
        val PAPER_WIDTH = 32
        val charset = Charset.forName("CP437")

        fun generateLine(char: Char = '-'): String = char.toString().repeat(PAPER_WIDTH) + "\n"
        fun twoColumnLine(left: String, right: String, leftWidthPercentage: Double = 0.65): String {
            val leftMax = ((PAPER_WIDTH -1) * leftWidthPercentage).toInt().coerceAtLeast(0)
            val rightMax = (PAPER_WIDTH - leftMax -1).coerceAtLeast(0)
            val l = left.take(leftMax)
            val r = right.take(rightMax)
            return String.format("%-${leftMax}s %${rightMax}s\n", l, r)
        }

        val sb = StringBuilder()
        val INIT_PRINTER = byteArrayOf(0x1B, 0x40)
        val BOLD_ON = byteArrayOf(0x1B, 0x45, 0x01)
        val BOLD_OFF = byteArrayOf(0x1B, 0x45, 0x00)
        val ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)
        val ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)

        sb.append(String(INIT_PRINTER, charset))
        sb.append(String(ALIGN_CENTER, charset))
        sb.append(String(BOLD_ON, charset))
        sb.append("Bayu Laundry\n")
        transaksi?.namaCabang?.let { sb.append("$it\n") }
        sb.append(String(BOLD_OFF, charset))
        sb.append(String(ALIGN_LEFT, charset))
        sb.append(generateLine())

        transaksi?.let { trx ->
            sb.append("ID Transaksi: ${trx.idTransaksi}\n")
            sb.append("Tanggal   : ${formatDate(trx.tanggalTransaksi)}\n")
            sb.append("Pelanggan : ${trx.namaPelanggan}\n")
            sb.append("Kasir     : ${trx.namaPegawai}\n")
            sb.append(generateLine())
            sb.append(twoColumnLine("Layanan:", "", 1.0))
            sb.append(twoColumnLine(trx.namaLayanan, formatRupiahForPrint(trx.hargaLayanan)))

            if (trx.layananTambahan.isNotEmpty()) {
                sb.append(generateLine('.'))
                sb.append("Layanan Tambahan:\n")
                trx.layananTambahan.forEachIndexed { index, tambahan ->
                    val nama = tambahan.namaTambahan ?: "N/A"
                    val harga = tambahan.hargaTambahan?.let { formatRupiahForPrint(it) } ?: "0"
                    sb.append(twoColumnLine(" ${index + 1}. $nama", harga))
                }
                sb.append(generateLine('.'))
                sb.append(twoColumnLine("Subtotal Tambahan:", formatRupiahForPrint(calculateSubtotalTambahan(trx.layananTambahan).toString())))
            }
            sb.append(generateLine())
            sb.append(String(BOLD_ON, charset))
            sb.append(twoColumnLine("TOTAL BAYAR:", formatRupiahForPrint(trx.totalHarga)))
            sb.append(String(BOLD_OFF, charset))
            sb.append(generateLine())
            sb.append("Status     : ${if (trx.statusPembayaran == "LUNAS") "LUNAS" else "BELUM DIBAYAR"}\n")
            sb.append("Pembayaran : ${getReadablePaymentMethod(trx.metodePembayaran)}\n\n")
            sb.append(String(ALIGN_CENTER, charset))
            sb.append("Terima Kasih!\n")
            sb.append("-- Bayu Laundry App --\n")
            sb.append(String(ALIGN_LEFT, charset))
        }
        sb.append("\n\n\n")
        return sb.toString()
    }


    private fun closeBluetoothConnection() {
        try { outputStream?.close() } catch (e: Exception) { Log.e("CloseBT", "Error closing outputStream: ${e.message}") }
        try { bluetoothSocket?.close() } catch (e: Exception) { Log.e("CloseBT", "Error closing socket: ${e.message}") }
        outputStream = null
        bluetoothSocket = null
    }

    override fun onDestroy() {
        super.onDestroy()
        closeBluetoothConnection()
        coroutineScope.cancel()
    }
}
