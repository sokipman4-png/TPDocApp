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
    const val ANALISIS = "analisis/{perusahaanId}"
    const val ONBOARDING = "onboarding"
    const val PANDUAN = "panduan"

    fun analisis(perusahaanId: Long): String = "analisis/$perusahaanId"

    fun formPerusahaan(perusahaanId: Long? = null): String =
        "form_perusahaan/${perusahaanId ?: 0}"

    fun detailPerusahaan(perusahaanId: Long): String =
        "detail_perusahaan/$perusahaanId"

    /** Rute layar utama untuk bottom navigation. */
    val TABS = listOf(DAFTAR_PERUSAHAAN, HOME, KALKULATOR, DOKUMEN, BERELASI)

    /** Semua rute statis (tanpa arg) yang didaflar di NavHost. */
    val STATICS = listOf(
        HOME, KRITERIA, KALKULATOR, BERELASI, TRANSAKSI, DOKUMEN, SANKSI,
        KESIMPULAN, NOTIFIKASI, CARI, DAFTAR_PERUSAHAAN, DASHBOARD, SETTINGS,
        ONBOARDING, PANDUAN,
    )

    /** Semua pattern rute (potentially dengan {arg}) — untuk validasi unik. */
    val ALL_PATTERNS: Set<String> = (STATICS + listOf(FORM_PERUSAHAAN, DETAIL_PERUSAHAAN, ANALISIS)).toSet()

    fun isTab(route: String?): Boolean = route in TABS
}