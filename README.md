🔐 Login Activity - Aplikasi Laundry Android

![image](https://github.com/user-attachments/assets/cd3d4f11-858b-467b-b532-ac678759d285)

Ini adalah halaman Login dalam aplikasi Android Laundry yang dibangun menggunakan Kotlin, Firebase Realtime Database, dan XML UI Design berbasis ConstraintLayout.

✨ Fitur Utama
Login menggunakan Email dan Password.

Otentikasi pengguna dari Firebase Realtime Database (/pegawai).

SharedPreferences digunakan untuk menyimpan sesi login pengguna.

Redirect otomatis ke MainActivity jika pengguna sudah login.

Akses ke halaman Registrasi.

🧱 Struktur Layout UI (XML)
💬 Header
Selamat Datang (judul)

Silakan login untuk melanjutkan (subjudul)

📝 Form Login
Email: EditText (editTextPhone)

Password: EditText (editTextPassword)

🔘 Aksi
ButtonLogin: Tombol untuk proses login.

tv_register_prompt: Teks untuk berpindah ke halaman registrasi (RegisterActivity).

💅 Desain
Container dibungkus dalam LinearLayout dengan bg_login_box, elevation, dan padding yang konsisten.

EditText menggunakan background khusus (bg_edittext) agar tampak modern dan bersih.

Layout sepenuhnya disusun dengan ConstraintLayout untuk fleksibilitas pada berbagai ukuran layar.

🔐 Logika Login (Kotlin - LoginActivity.kt)
🧾 Langkah-langkah:
Validasi input: email & password tidak boleh kosong.

Query Firebase: Cari data pengguna dengan email yang sesuai.

Verifikasi:

Jika ditemukan & password cocok: login berhasil, data disimpan di SharedPreferences.

Jika tidak cocok atau tidak ditemukan: tampilkan pesan kesalahan.

Redirect ke MainActivity jika berhasil.

🔒 Penyimpanan Sesi:
val editor = sharedPref.edit()
editor.putString("idPegawai", pegawai.idPegawai)
editor.putString("namaPegawai", pegawai.namaPegawai)
editor.putString("idCabang", pegawai.idCabang)
editor.putBoolean("isLoggedIn", true)
editor.apply()

📦 Auto-login:
Jika pengguna sudah login sebelumnya, halaman ini akan otomatis mengarahkan ke MainActivity pada onStart().

📚 Dependensi & Tools
Firebase Realtime Database

Kotlin Android Extensions

AndroidX

ViewCompat, WindowInsetsCompat untuk handling insets pada device modern

📁 Resource yang Digunakan
Drawable:

@drawable/bg_login_box

@drawable/bg_edittext

Color:

@color/white, @color/bglogin

String Resource (di res/values/strings.xml):

@string/slmtdtg: "Selamat Datang"

@string/slum: "Silakan login untuk melanjutkan"

@string/bpads: "Belum punya akun? Daftar di sini"

📌 Catatan Tambahan
Pastikan struktur data di Firebase menggunakan child "email" dan "password" dalam node pegawai.

Kelas model_pegawai harus memiliki properti: idPegawai, namaPegawai, idCabang, email, password.

Gunakan proteksi tambahan seperti hashing password di produksi.





🏠 MainActivity - Aplikasi Laundry Android

![image](https://github.com/user-attachments/assets/32859efc-9433-4882-a346-f9a4e7275085)

MainActivity merupakan halaman beranda utama aplikasi Laundry Android. Halaman ini menampilkan informasi umum seperti salam waktu, tanggal, estimasi pendapatan, serta menu navigasi cepat ke berbagai modul seperti Transaksi, Pegawai, Pelanggan, dan lainnya.

🔧 Fitur Utama
✅ Salam otomatis berdasarkan waktu (pagi, siang, sore, malam)

📆 Tanggal otomatis ditampilkan menggunakan LocalDate

💰 Estimasi Pendapatan Harian ditampilkan dalam CardView

🧭 Navigasi Menu ke:

Data Pelanggan

Data Pegawai

Data Layanan

Data Tambahan

Data Cabang

Data Transaksi

Data Laporan

Data Akun

🔒 Cek sesi login otomatis via SharedPreferences

🖼️ Struktur Tampilan (UI)
1. Header
TextView sapaan (contoh: "Selamat Pagi")

TextView tanggal (contoh: "21 Januari 2025")

2. Estimasi Pendapatan
CardView dengan teks estimasi dan nilai estimasi (hardcoded "Rp. 40.000,-")

Ikon menu horizontal: Transaksi, Pelanggan, Laporan

3. Reminder
"Siap melayani pelanggan hari ini"

"Jangan kecewakan pelanggan"

4. Menu Grid (2x3)
Berisi ikon dan label:

Akun

Layanan

Tambahan

Pegawai

Cabang

Printer

Setiap menu dikemas dalam CardView dengan efek elevation dan sudut membulat.

📂 Struktur Kode: MainActivity.kt
🔄 Fungsi Dinamis
getCurrentDate(): Mengembalikan tanggal hari ini dalam format "dd MMMM yyyy"

getGreetingMessage(): Mengembalikan salam berdasarkan jam saat ini

🔁 Redirect Otomatis
override fun onStart() {
    val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
    if (!sharedPref.getBoolean("isLoggedIn", false)) {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

🧭 Navigasi Menu
Setiap menu (mis. pelanggan_menu) akan membuka Activity yang sesuai:

val pelangganMenu = findViewById<LinearLayout>(R.id.pelanggan_menu)
pelangganMenu.setOnClickListener {
    val intent = Intent(this, DataPelangganActivity::class.java)
    startActivity(intent)
}

🎨 Resource yang Digunakan
Drawable
@drawable/ic_transaksi

@drawable/ic_pelanggan

@drawable/ic_laporan

@drawable/ic_pegawai

@drawable/ic_layanan

@drawable/ic_tambahan

@drawable/ic_akun

@drawable/ic_cabang

@drawable/ic_printer

String Resource (res/values/strings.xml)
@string/selamatpagi

@string/card_estimasi

@string/text_Transaksi

@string/text_pelanggan

@string/text_laporan

@string/text_pegawai

@string/text_layanan

@string/text_tambahan

@string/text_akun

@string/card_cabang

@string/text_printer

@string/text_siapmelayani

@string/text_jangankecewakanpelanggan

📱 Teknologi yang Digunakan
Kotlin + AndroidX

ConstraintLayout & CardView

SharedPreferences untuk sesi login

Intent untuk navigasi antar aktivitas

LocalTime & LocalDate untuk greeting & tanggal





👤 DataAkunActivity - Aplikasi Laundry Android

![image](https://github.com/user-attachments/assets/74b67c8c-73f5-4000-af7d-ac761b4f82ad)

DataAkunActivity adalah halaman profil pegawai yang menampilkan informasi pribadi pengguna yang sedang login, serta menyediakan tombol untuk logout dari sesi aplikasi.

📌 Fitur Utama
🔍 Menampilkan informasi lengkap pegawai:

ID Pegawai

Nama

Email

Nomor HP

Alamat

Cabang

💾 Mengambil data langsung dari Firebase Realtime Database.

🗂️ Menggunakan SharedPreferences untuk identifikasi sesi pengguna.

🚪 Tombol Logout untuk menghapus sesi dan kembali ke halaman login.

🧠 Alur Kerja
🔁 Saat onCreate():
Inisialisasi tampilan (initViews()).

Ambil data dari SharedPreferences.

Query ke Firebase berdasarkan idPegawai.

Tampilkan data ke UI.

📤 Logout:
Saat tombol btnLogout diklik:

Membersihkan semua data SharedPreferences.

Redirect ke halaman login (LoginActivity) dan mengakhiri aktivitas.

💡 Struktur Kode
🔑 Shared Preferences
sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)

📥 Mengambil data dari Firebase
database.child(idPegawai).addListenerForSingleValueEvent(...)

📤 Logout
val editor = sharedPref.edit()
editor.clear()
editor.apply()
startActivity(Intent(this, LoginActivity::class.java))
finish()

🖼️ Layout XML
Layout menggunakan struktur LinearLayout vertikal dengan:

TextView Judul: "Profil Pegawai"

CardView yang berisi label dan isi data pengguna

Tombol logout di bagian bawah

Komponen UI Penting:
Komponen	ID	Keterangan
Nama	tv_akun_nama	Menampilkan nama pegawai
Email	tv_akun_email	Email pegawai
No HP	tv_akun_nohp	Nomor telepon
Alamat	tv_akun_alamat	Alamat lengkap
ID Pegawai	tv_akun_id_pegawai	ID unik pengguna
Cabang	tv_akun_cabang	Lokasi cabang
Tombol Logout	btn_logout	Keluar dari sesi dan kembali login

🎨 Desain & Styling
CardView digunakan untuk membungkus data pegawai agar tampak bersih dan profesional.

TextView label menggunakan textSize="12sp", sedangkan nilainya 16sp dan bold.

Button logout menggunakan @color/purple_700 dengan teks putih.

📦 Dependensi yang Digunakan
Pastikan Anda telah menambahkan dependensi berikut di build.gradle:

gradle
implementation 'com.google.firebase:firebase-database'
implementation 'androidx.cardview:cardview:1.0.0'

⚠️ Catatan Tambahan
Class model_pegawai harus memiliki field: idPegawai, namaPegawai, email, noHPPegawai, alamatPegawai, idCabang.

Data disediakan oleh Firebase Realtime Database di path: pegawai/{idPegawai}.

Tombol logout membersihkan semua data sesi tanpa konfirmasi tambahan.





🧺 Data & Tambah Layanan - Aplikasi Laundry Android
Modul ini mencakup dua bagian utama:

![image](https://github.com/user-attachments/assets/d0a92918-3aeb-497c-a776-d2357639bc08)

DataLayananActivity: Menampilkan daftar layanan laundry dari Firebase Realtime Database.

![image](https://github.com/user-attachments/assets/41952778-16f4-4004-8ea0-df0c9deaf2b1)


TambahLayananActivity: Menambahkan layanan baru ke database.

🚀 Fitur
📄 DataLayananActivity
Menampilkan layanan laundry (nama, harga, cabang) dalam RecyclerView.

Menyimpan data layanan secara real-time dari Firebase.

Menampilkan teks "data kosong" jika tidak ada layanan.

Tombol FloatingActionButton untuk menambahkan layanan baru.

➕ TambahLayananActivity
Form input untuk:

Nama Layanan

Harga Layanan

Cabang

Validasi setiap field wajib diisi.

Menyimpan data ke Firebase dengan key unik.

Menampilkan notifikasi sukses/gagal menggunakan Toast.

🧱 Struktur File & Layout
🔧 activity_data_layanan.xml
xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/rvDATA_LAYANAN"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />

<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:id="@+id/fabDATA_PENGGUNA_TambahLayanan"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@drawable/add" />
🔧 activity_tambah_layanan.xml
xml
<EditText android:id="@+id/etlayanannama" />
<EditText android:id="@+id/etlayananharga" />
<EditText android:id="@+id/etlayanancabang" />
<Button android:id="@+id/btlayanansimpan" android:text="Simpan" />
🧠 Kode Penting
🔄 Ambil Data Layanan dari Firebase
kotlin
val query = myRef.orderByChild("idLayanan").limitToLast(100)
query.addValueEventListener(object : ValueEventListener { ... })
✅ Validasi Tambah Layanan
kotlin
if (nama.isEmpty()) {
    etNama.error = "Nama harus diisi"
    return
}
💾 Simpan ke Firebase
kotlin
val data = model_layanan(layananid, nama, harga, cabang)
layananBaru.setValue(data)
📦 Model Data: model_layanan.kt
kotlin
data class model_layanan(
    val idLayanan: String = "",
    val namaLayanan: String = "",
    val hargaLayanan: String = "",
    val idCabang: String = ""
)
🖼️ Adapter
Pastikan Anda memiliki adapter AdapterDataLayanan.kt untuk menampilkan daftar layanan di RecyclerView. Biasanya bentuknya seperti:

kotlin
class AdapterDataLayanan(private val list: List<model_layanan>) :
    RecyclerView.Adapter<AdapterDataLayanan.ViewHolder>() {
    ...
}
📚 Dependensi
Tambahkan ini di build.gradle:

gradle
implementation 'com.google.firebase:firebase-database'
implementation 'androidx.recyclerview:recyclerview:1.3.1'
implementation 'com.google.android.material:material:1.11.0'
🔐 Permissions
Tambahkan koneksi internet di AndroidManifest.xml:

xml
Copy
Edit
<uses-permission android:name="android.permission.INTERNET" />


