package com.tpdoc.app.data.room

import android.content.Context
import kotlinx.coroutines.flow.Flow

/** Repository checklist dengan seeding data default sekali pakai. */
class ChecklistRepository(context: Context) {

    private val dao = AppDatabase.get(context).checklistDao()

    val semua: Flow<List<ChecklistItem>> = dao.observeAll()
    val berelasi: Flow<List<ChecklistItem>> = dao.observeByCategory(ChecklistItem.CAT_BERELASI)
    val dokumen: Flow<List<ChecklistItem>> = dao.observeByCategory(ChecklistItem.CAT_DOKUMEN)
    val persiapan: Flow<List<ChecklistItem>> = dao.observeByCategory(ChecklistItem.CAT_PERSIAPAN)

    suspend fun seedIfEmpty() {
        if (dao.countAll() == 0) {
            dao.insertAll(DefaultChecklist.items)
        }
    }

    suspend fun toggle(item: ChecklistItem) = dao.setChecked(item.id, !item.checked)

    suspend fun resetAll() = dao.clearAll()

    suspend fun reseed() {
        dao.clearAll()
        dao.insertAll(DefaultChecklist.items)
    }
}

object DefaultChecklist {

    val items: List<ChecklistItem> = listOf(
        // ---- Pihak berelasi (indikator hubungan istimewa) ----
        ChecklistItem(
            category = ChecklistItem.CAT_BERELASI,
            title = "Kepemilikan saham >= 25%",
            desc = "Satu pihak memiliki >= 25% saham (dengan hak suara) di pihak lain, langsung/tidak langsung.",
        ),
        ChecklistItem(
            category = ChecklistItem.CAT_BERELASI,
            title = "Keluarga sedarah/semenda derajat 2",
            desc = "Ada hubungan keluarga sedarah atau semenda sampai derajat kedua.",
        ),
        ChecklistItem(
            category = ChecklistItem.CAT_BERELASI,
            title = "Direksi/komisaris yang sama",
            desc = "Orang yang sama menduduki jabatan direksi atau komisaris di kedua perusahaan.",
        ),
        ChecklistItem(
            category = ChecklistItem.CAT_BERELASI,
            title = "Ketergantungan keuangan/teknis",
            desc = "Contoh: 1 supplier memasok 90% barangnya ke kamu.",
        ),

        // ---- Dokumen TP ----
        ChecklistItem(
            category = ChecklistItem.CAT_DOKUMEN,
            title = "Master File",
            desc = "Gambaran grup usaha global. Wajib jika omzet grup > Rp50 M dan transaksi afiliasi > Rp20 M.",
        ),
        ChecklistItem(
            category = ChecklistItem.CAT_DOKUMEN,
            title = "Local File",
            desc = "Detail transaksi lokal + analisis kewajaran (FAR, metode TP, benchmark).",
        ),
        ChecklistItem(
            category = ChecklistItem.CAT_DOKUMEN,
            title = "CbCR",
            desc = "Laporan per negara. Wajib jika omzet konsolidasi grup > Rp11 T.",
        ),
        ChecklistItem(
            category = ChecklistItem.CAT_DOKUMEN,
            title = "Notifikasi CbCR",
            desc = "Disampaikan elektronik maksimal 12 bulan setelah akhir tahun pajak.",
        ),
        ChecklistItem(
            category = ChecklistItem.CAT_DOKUMEN,
            title = "Lampiran SPT 3A-1, 3A-2, 3B",
            desc = "Formulir lampiran SPT Tahunan Badan terkait transaksi pihak berelasi.",
        ),

        // ---- Persiapan kepatuhan ----
        ChecklistItem(
            category = ChecklistItem.CAT_PERSIAPAN,
            title = "Identifikasi transaksi dengan pihak berelasi",
            desc = "Petakan semua transaksi afiliasi: jual beli, jasa, pinjaman, royalti, sewa.",
        ),
        ChecklistItem(
            category = ChecklistItem.CAT_PERSIAPAN,
            title = "Hitung threshold kewajiban dokumen",
            desc = "Omzet konsolidasi grup vs Rp50 M / Rp11 T dan transaksi afiliasi vs Rp20 M.",
        ),
        ChecklistItem(
            category = ChecklistItem.CAT_PERSIAPAN,
            title = "Terapkan prinsip kewajaran (ALP)",
            desc = "Pastikan harga transaksi sama seperti transaksi dengan pihak ketiga yang wajar.",
        ),
        ChecklistItem(
            category = ChecklistItem.CAT_PERSIAPAN,
            title = "Siapkan dokumentasi & benchmark",
            desc = "Kumpulkan data pembanding (benchmark) pihak independen sebagai bukti kewajaran.",
        ),
        ChecklistItem(
            category = ChecklistItem.CAT_PERSIAPAN,
            title = "Cek batas waktu notifikasi CbCR",
            desc = "Maksimal 12 bulan setelah akhir tahun pajak, disampaikan secara elektronik.",
        ),
    )
}