# 📅 Faithful Streak

**Faithful Streak** adalah aplikasi Android sederhana yang dirancang untuk membantu kamu membangun kebiasaan baik dan mempertahankan streak harian — khususnya dalam konteks pertumbuhan iman dan disiplin pribadi.

Aplikasi ini **tidak mengumpulkan data apa pun** dan berjalan sepenuhnya **offline** di perangkat kamu.

---

## ✨ Fitur Utama

- 🔥 **Sistem Streak Harian** — tandai hari kamu setiap kali berhasil menjalani komitmenmu.
- 📈 **Target & Progress Bar** — tetapkan target (misal 7 hari, 30 hari, dst.) dan lihat progresmu secara visual.
- 📅 **Riwayat Streak** — pantau perjalanan kamu dari awal hingga sekarang.
- 📖 **Ayat Motivasi Harian** — setiap check-in akan menampilkan ayat baru secara acak.
- 💭 **Dialog Motivasi** — ketika relapse, aplikasi akan menampilkan pesan semangat dan ayat untuk bangkit kembali.
- 🌙 **Desain Modern (Jetpack Compose)** — tampilan ringan, modern, dan smooth tanpa library berlebihan.
- 🧠 **Tanpa Akun, Tanpa Iklan, Tanpa Internet** — semua data tersimpan aman secara lokal.

---

## 🧩 Teknologi yang Digunakan

- **Kotlin + Jetpack Compose**
- **Room Database** untuk penyimpanan riwayat streak
- **DataStore Preferences** untuk state dan konfigurasi
- **Lottie Compose** untuk animasi
- **MVVM Architecture** dengan ViewModel
- **Material 3 (Material You)** untuk tampilan modern

---

## 📦 Rilis Aplikasi

Versi terbaru: **v1.0**

📥 **[Unduh Faithful Streak v1.0 (APK)](https://github.com/GabrielNathanael/faithfulstreakapp/raw/main/app/build/outputs/apk/release/FaithfulStreak-release.apk)**

> Aplikasi ini sudah ditandatangani (release-signed) dan bisa langsung diinstall di perangkat Android 8.0 (Oreo) ke atas.

---

## 🛠️ Cara Build Manual

Jika kamu ingin membangun sendiri dari source code:

1. Pastikan sudah menginstall:
   - Android Studio (Ladybug+)
   - JDK 17
   - Android SDK 35
2. Clone repository:
   ```bash
   git clone https://github.com/<username>/FaithfulStreak.git
   cd FaithfulStreak
   ```
