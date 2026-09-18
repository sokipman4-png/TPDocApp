package com.tpdoc.app.calc

/**
 * Kalkulator threshold kewajiban TP Doc sesuai dokumen & spesifikasi misi.
 *
 * Aturan:
 *  - Master File + Local File: omzet konsolidasi grup > Rp50 M **DAN** transaksi afiliasi > Rp20 M
 *  - CbCR (dan notifikasinya): omzet konsolidasi grup > Rp11 T
 *  - Batas memakai strictly greater (`>`): persis di angka threshold belum wajib.
 */
object ThresholdCalculator {

    const val OMSET_MASTER_LOCAL: Long = 50_000_000_000L        // Rp50 miliar
    const val TRANSAKSI_AFILIASI_LOCAL: Long = 20_000_000_000L  // Rp20 miliar
    const val OMSET_CBCR: Long = 11_000_000_000_000L            // Rp11 triliun

    data class Result(
        val omzetGrup: Long,
        val transaksiAfiliasi: Long,
        val wajibMasterLocal: Boolean,
        val wajibCbcr: Boolean,
        val alasanMasterLocal: String,
        val alasanCbcr: String,
    )

    fun hitung(omzetGrup: Long, transaksiAfiliasi: Long): Result {
        val omzet = omzetGrup.coerceAtLeast(0L)
        val transaksi = transaksiAfiliasi.coerceAtLeast(0L)

        val wajibMasterLocal = omzet > OMSET_MASTER_LOCAL && transaksi > TRANSAKSI_AFILIASI_LOCAL
        val alasanMasterLocal = buildString {
            append("Omzet grup ")
            append(jikaTidak(omzet > OMSET_MASTER_LOCAL))
            append(" melebihi Rp50 M")
            append(" dan transaksi afiliasi ")
            append(jikaTidak(transaksi > TRANSAKSI_AFILIASI_LOCAL))
            append(" melebihi Rp20 M.")
            append("  -> ")
            append(if (wajibMasterLocal) "WAJIB membuat Master File & Local File." else "TIDAK wajib dokumen formal (tapi prinsip kewajaran tetap berlaku).")
        }

        val wajibCbcr = omzet > OMSET_CBCR
        val alasanCbcr = buildString {
            append("Omzet grup ")
            append(jikaTidak(wajibCbcr))
            append(" melebihi Rp11 T.")
            append("  -> ")
            append(if (wajibCbcr) "WAJIB menyampaikan CbCR beserta notifikasinya." else "Tidak wajib CbCR.")
        }

        return Result(omzet, transaksi, wajibMasterLocal, wajibCbcr, alasanMasterLocal, alasanCbcr)
    }

    private fun jikaTidak(kondisi: Boolean): String = if (kondisi) "sudah" else "belum"
}