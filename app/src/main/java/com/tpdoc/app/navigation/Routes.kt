package com.tpdoc.app.navigation

object Routes {
    const val HOME = "home"
    const val KRITERIA = "kriteria"
    const val KALKULATOR = "kalkulator"
    const val BERELASI = "berelasi"
    const val TRANSAKSI = "transaksi"
    const val DOKUMEN = "dokumen"
    const val SANKSI = "sanksi"
    const val KESIMPULAN = "kesimpulan"
    const val NOTIFIKASI = "notifikasi"
    const val CARI = "cari"

    /** Rute layar utama yang tampil di bottom navigation. */
    val TABS = listOf(HOME, KALKULATOR, DOKUMEN, BERELASI, CARI)

    fun isTab(route: String?): Boolean = route in TABS
}