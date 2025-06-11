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

🔐 Permissions
Tambahkan koneksi internet di AndroidManifest.xml:





➕ Data & Tambah Tambahan - Aplikasi Laundry Android

![image](https://github.com/user-attachments/assets/58be904f-3829-4114-a7c8-452da398813e)


![image](https://github.com/user-attachments/assets/d0d4e680-25ed-443a-911f-4cb7bf0bc42a)



Modul ini merupakan bagian dari sistem manajemen tambahan biaya atau layanan dalam aplikasi Laundry. Terdiri dari dua aktivitas utama: DataTambahanActivity untuk menampilkan data tambahan dan TambahTambahanActivity untuk menambahkan entri baru.

🗂️ Fitur: DataTambahanActivity
Menampilkan daftar tambahan layanan dari Firebase Realtime Database.

Menggunakan tampilan berbasis RecyclerView.

Data ditampilkan dari urutan terbaru ke terlama.

Jika data tidak ada, tampilan tetap rapi dan tidak menampilkan kesalahan.

Terdapat tombol Floating Action Button (FAB) untuk menambahkan data tambahan baru.

➕ Fitur: TambahTambahanActivity
Form untuk input data tambahan:

Nama tambahan

Harga

Cabang terkait

Dilengkapi validasi input:

Semua field wajib diisi sebelum penyimpanan.

Menampilkan notifikasi jika data tidak lengkap.

Data disimpan ke Firebase Realtime Database menggunakan key unik.

Terdapat notifikasi berupa pesan sukses atau gagal setelah proses simpan dilakukan.

🏗️ Struktur Layout
activity_data_tambahan.xml:

Mengandung RecyclerView untuk daftar tambahan.

FloatingActionButton di pojok kanan bawah untuk membuka halaman tambah.

activity_tambah_tambahan.xml (diasumsikan sesuai konvensi):

Tiga input: nama tambahan, harga, dan cabang.

Tombol simpan untuk menyimpan data ke Firebase.

📦 Data Model
Data tambahan disimpan ke node tambahan pada Firebase, dan diasumsikan memiliki atribut:

ID Tambahan

Nama Tambahan

Harga

Cabang

🔐 Validasi dan Penyimpanan
Sistem memastikan bahwa tidak ada data kosong yang disimpan. Setiap data baru disimpan dengan ID unik yang dihasilkan oleh Firebase. Saat penyimpanan berhasil atau gagal, pengguna akan menerima notifikasi visual (toast).

📝 Catatan Tambahan
Aktivitas ini saling terhubung: tombol tambah pada DataTambahanActivity akan membuka TambahTambahanActivity.

Untuk pengelolaan data lebih lanjut (seperti edit dan hapus), Anda dapat memperluas fungsionalitas adapter.

Pastikan resource string seperti validasi dan pesan sukses/gagal sudah ditambahkan di strings.xml.





👥 Data & Tambah/Edit Pegawai - Aplikasi Laundry Android
Modul ini digunakan untuk mengelola data pegawai dalam sistem aplikasi Laundry. Terdiri dari dua aktivitas utama:

![image](https://github.com/user-attachments/assets/a94d4493-02cd-46b0-b4fa-b48488ae2c9c)


DataPegawaiActivity – Menampilkan seluruh daftar pegawai dari database.

![image](https://github.com/user-attachments/assets/4c2e9af8-a01b-4e24-91ca-5fd6ef7a7dd6)


TambahPegawaiActivity – Menambahkan pegawai baru atau mengedit data pegawai yang sudah ada.

🔍 Fitur: DataPegawaiActivity
Menampilkan data pegawai dari Firebase Realtime Database menggunakan RecyclerView.

Data ditampilkan berdasarkan waktu input terakhir (data terbaru muncul di atas).

Dilengkapi dengan Floating Action Button (FAB) di pojok kanan bawah untuk menambahkan pegawai baru.

Data dimuat secara real-time dari node pegawai.

Bila proses pengambilan data gagal, pengguna akan diberi notifikasi melalui pesan toast.

➕ Fitur: TambahPegawaiActivity
Form input data pegawai yang terdiri dari:

Nama

Alamat

Nomor HP

Email

Password

Pilihan cabang (melalui PilihCabangActivity)

Bisa digunakan untuk:

Menambahkan pegawai baru

Mengedit data pegawai (jika data dikirim lewat Intent)

Validasi akan memastikan semua data diisi sebelum disimpan atau diperbarui.

Menyimpan data baru menggunakan push ID dari Firebase.

Pembaruan data menggunakan metode updateChildren dengan referensi ke idPegawai.

Notifikasi kesuksesan atau kegagalan disampaikan melalui toast.

🧱 Struktur Tampilan (Layout)
activity_data_pegawai.xml:

Terdiri dari judul halaman, daftar pegawai (RecyclerView), dan tombol tambah (FAB).

activity_tambah_pegawai.xml:

Memuat form lengkap untuk input data pegawai dan tombol pilih cabang.

Terdapat teks untuk menampilkan ID atau nama cabang yang telah dipilih.

🧩 Alur Tambah/Edit Pegawai
Jika pengguna membuka halaman dengan intent kosong → mode tambah data.

Jika idPegawai dikirimkan melalui intent → mode edit:

Field akan diisi otomatis dengan data yang dikirim.

Tombol simpan berubah menjadi tombol update.

Setelah disimpan atau diperbarui, aktivitas ditutup dan pengguna kembali ke halaman sebelumnya.

📦 Kebutuhan Tambahan
Adapter bernama AdapterDataPegawai untuk menampilkan data dalam RecyclerView.

Layout kartu card_data_pegawai.xml untuk menampilkan setiap pegawai.

Aktivitas PilihCabangActivity yang mengembalikan data cabang terpilih ke TambahPegawaiActivity.

📝 Catatan
Pastikan semua string validasi, pesan toast, dan label tombol sudah tersedia di strings.xml.

Struktur data pada Firebase harus sesuai dengan model model_pegawai.

Jangan lupa izin koneksi internet harus tersedia dalam AndroidManifest.xml.




