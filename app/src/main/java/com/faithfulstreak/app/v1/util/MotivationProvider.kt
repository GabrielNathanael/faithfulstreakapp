package com.faithfulstreak.app.v1.util

import kotlin.random.Random

object MotivationProvider {
    private val messages = listOf(
        "Kasih karunia Tuhan cukup bagimu. Bangkit dan coba lagi dengan kekuatan-Nya!",
        "Tuhan tidak pernah meninggalkan kita. Kembalilah kepada-Nya, Dia setia mengampuni.",
        "Dalam kelemahan kita, kuasa Kristus menjadi sempurna. Jangan menyerah!",
        "Setiap hari adalah anugerah baru dari Tuhan. Mulai lagi dengan iman.",
        "Kristus telah membebaskan kita. Berpeganglah pada kebebasan itu dan bangkit!",
        "Tuhan lebih besar dari kejatuhanmu. Dia mampu memulihkan semuanya.",
        "Iman bukan tentang tidak pernah jatuh, tapi tentang selalu kembali kepada Tuhan.",
        "Doa kecil yang tulus bisa menyalakan kembali api yang mulai padam.",
        "Setiap langkah kecil menuju ketaatan berarti besar di mata Tuhan.",
        "Tuhan tahu perjuanganmu, dan Dia tidak malu menyebutmu anak-Nya.",
        "Kegagalan hari ini tidak menentukan masa depanmu bersama Tuhan.",
        "Saat kau lemah, biarkan Tuhan yang kuat menopangmu.",
        "Hidup kudus bukan hasil kerja keras, tapi hasil penyerahan yang terus diperbarui.",
        "Percayalah, kasih Tuhan tidak habis karena kesalahanmu.",
        "Hari ini kesempatan baru untuk menjadi lebih dekat dengan Kristus.",
        "Kemenangan sejati bukan berarti tak pernah jatuh, tapi terus mau berdiri lagi.",
        "Setiap luka yang disembuhkan Tuhan akan jadi kesaksian bagi orang lain.",
        "Bahkan di saat gelap, cahaya kasih Tuhan tidak pernah padam.",
        "Jangan menilai dirimu dari kejatuhanmu, tapi dari siapa yang menebusmu.",
        "Roh Kudus yang sama yang membangkitkan Kristus juga bekerja dalam dirimu.",
        "Ketaatan kecil hari ini bisa menyalakan perubahan besar besok.",
        "Tuhan tidak mencari kesempurnaanmu, Dia mencari hatimu.",
        "Jika api imanmu mulai redup, datanglah lebih dekat kepada Sumber Terang.",
        "Setiap kali kamu kembali, surga bersukacita.",
        "Tuhan masih bekerja, bahkan ketika kamu merasa tidak layak.",
        "Jangan takut memulai dari awal; Tuhan suka dengan hati yang mau dibentuk.",
        "Kasih Tuhan tidak pernah berkurang walau kamu tersandung berkali-kali.",
        "Kamu tidak sendirian dalam perjalanan ini; Tuhan berjalan bersamamu.",
        "Biar hatimu jatuh seribu kali, biarkan kasih Tuhan mengangkatnya lagi.",
        "Kemenangan sejati dimulai ketika kamu berkata, ‘Tuhan, aku butuh Engkau.’"
    )

    fun random(): String = messages[Random.nextInt(messages.size)]
}
