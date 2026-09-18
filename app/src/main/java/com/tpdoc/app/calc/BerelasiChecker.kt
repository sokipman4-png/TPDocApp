package com.tpdoc.app.calc

/**
 * Logika penentuan "terindikasi pihak berelasi" dari checklist kriteria hubungan
 * istimewa (dokumen baris 15-19).
 *
 * Jika SATU saja kriteria terpenuhi, pihak dianggap berelasi -> transaksinya
 * tunduk pada prinsip kewajaran Transfer Pricing.
 */
object BerelasiChecker {

    data class Kriteria(
        val kode: String,
        val judul: String,
        val detail: String,
    )

    val KRITERIA = listOf(
        Kriteria(
            "B01",
            "Kepemilikan saham >= 25%",
            "Satu pihak memiliki saham >= 25% (dengan hak suara) di pihak lain, baik langsung maupun tidak langsung (pengendalian).",
        ),
        Kriteria(
            "B02",
            "Keluarga sedarah / semenda derajat 2",
            "Ada hubungan keluarga sedarah atau semenda sampai derajat kedua antara para pihak.",
        ),
        Kriteria(
            "B03",
            "Direksi / komisaris yang sama",
            "Ada orang yang sama menduduki jabatan direksi atau komisaris di kedua perusahaan.",
        ),
        Kriteria(
            "B04",
            "Ketergantungan keuangan / teknis",
            "Hubungan usaha berupa ketergantungan keuangan atau teknis. Contoh: 1 supplier memasok 90% barang ke kamu.",
        ),
    )

    /** Indikasi berelasi = minimal satu kriteria terpenuhi. */
    fun terindikasiBerelasi(terpenuhi: List<Boolean>): Boolean = terpenuhi.any { it }

    fun jumlahTerpenuhi(terpenuhi: List<Boolean>): Int = terpenuhi.count { it }
}