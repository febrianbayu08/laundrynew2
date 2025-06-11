🔐 Login Activity - Aplikasi Laundry Android
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
kotlin
Copy
Edit
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

📸 Screenshot 
![WhatsApp Image 2025-06-11 at 15 59 25_4f99db35](https://github.com/user-attachments/assets/6803a23d-9497-4e31-99c5-1c67d8439b0a)





🏠 MainActivity - Aplikasi Laundry Android
![WhatsApp Image 2025-06-11 at 15 54 06_fa9ac052](https://github.com/user-attachments/assets/1f162a2f-c8ca-4a59-bf43-c581ec264c27)

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
kotlin
Copy
Edit
override fun onStart() {
    val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
    if (!sharedPref.getBoolean("isLoggedIn", false)) {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
🧭 Navigasi Menu
Setiap menu (mis. pelanggan_menu) akan membuka Activity yang sesuai:

kotlin
Copy
Edit
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


