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

    // V2 routes
    const val DAFTAR_PERUSAHAAN = "daftar_perusahaan"
    const val FORM_PERUSAHAAN = "form_perusahaan/{perusahaanId}"
    const val DETAIL_PERUSAHAAN = "detail_perusahaan/{perusahaanId}"
    const val DASHBOARD = "dashboard"
    const val SETTINGS = "settings"

    fun formPerusahaan(perusahaanId: Long? = null): String =
        "form_perusahaan/${perusahaanId ?: 0}"

    fun detailPerusahaan(perusahaanId: Long): String =
        "detail_perusahaan/$perusahaanId"

    /** Rute layar utama yang tampil di bottom navigation. */
    val TABS = listOf(DAFTAR_PERUSAHAAN, HOME, KALKULATOR, DOKUMEN, BERELASI)

    fun isTab(route: String?): Boolean = route in TABS
}