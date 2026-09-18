package com.tpdoc.app.data

import com.tpdoc.app.navigation.Routes

/**
 * Basis pengetahuan statis aplikasi — seluruh isi TP Doc.docx dalam struktur
 * yang bisa dirender ke UI dan dicari lewat layar Pencarian.
 */
object ContentData {

    // ---------- Home ----------
    val pengertianTP = "Transfer Pricing di Indonesia wajib diterapkan jika ada transaksi antara pihak yang " +
        "mempunyai hubungan istimewa (afiliasi). Yang WAJIB menerapkan transfer pricing adalah semua " +
        "WP Badan yang punya transaksi dengan pihak berelasi."

    val prinsipKewajaran = "Prinsip Kewajaran & Kelaziman Usaha (Arm's Length Principle): harga transaksi " +
        "harus sama seperti kalau bertransaksi dengan pihak ketiga yang independen."

    val dasarHukum = listOf(
        "PMK 172/2023" to "PMK Nomor 172 Tahun 2023 tentang Penerapan Prinsip Kewajaran dan Kelaziman Usaha dalam Transaksi yang Dipengaruhi Hubungan Istimewa",
        "PER-22/PJ/2023" to "Peraturan Direktur Jenderal Pajak Nomor PER-22/PJ/2023 tentang Tata Cara Penerapan Prinsip Kewajaran dan Kelaziman Usaha",
    )

    // ---------- Kriteria Wajib ----------
    data class KriteriaWajib(val judul: String, val rincian: String)

    val kriteriaWajib = listOf(
        KriteriaWajib(
            "Perusahaan Multinasional",
            "Punya induk/anak perusahaan di luar negeri. Contoh: holding di Singapore punya anak PT di Indonesia.",
        ),
        KriteriaWajib(
            "Holding & anak sama-sama di Indonesia",
            "Tetap wajib TP Doc. Contoh: PT A pegang 80% saham PT B, lalu PT A tagih management fee ke PT B.",
        ),
        KriteriaWajib(
            "Punya cabang / SKP di luar negeri",
            "Kantor pusat di Indonesia bertransaksi dengan kantor cabang (atau tempat usaha tetap/SKP) di Malaysia, Singapura, dll.",
        ),
    )

    val catatanKriteria =
        "Catatan: walau belum sampai threshold, prinsip kewajaran tetap wajib. DJP tetap bisa koreksi kalau harga tidak wajar."

    // ---------- Jenis Transaksi ----------
    data class JenisTransaksi(val nama: String, val keterangan: String)

    val jenisTransaksi = listOf(
        JenisTransaksi("Jual beli barang", "Anak perusahaan jual raw material ke holding."),
        JenisTransaksi("Jasa Manajemen / Management Fee", "Holding tagih biaya ke anak perusahaan untuk HR, Finance, IT."),
        JenisTransaksi("Pinjam-meminjam / Bunga", "Holding pinjamkan dana ke anak perusahaan (intra-group financing)."),
        JenisTransaksi("Royalti / Lisensi Merek", "Bayar royalti ke induk di luar negeri."),
        JenisTransaksi("Sewa", "Sewa kantor, gudang, mesin antar perusahaan grup."),
    )

    // ---------- Dokumen TP ----------
    data class DokumenInfo(
        val kode: String,
        val nama: String,
        val tujuan: String,
        val isiUtama: List<String>,
        val kapanWajib: String,
    )

    val dokumenTp = listOf(
        DokumenInfo(
            kode = "MF",
            nama = "Master File",
            tujuan = "Dokumen berisi informasi makro dan menyeluruh mengenai struktur bisnis grup usaha " +
                "multinasional secara global. Memberikan gambaran besar (cetak biru bisnis grup) kepada otoritas pajak.",
            isiUtama = listOf(
                "Struktur kepemilikan grup usaha dan bagan organisasi global",
                "Kegiatan usaha utama, lini produk, dan rantai pasokan (supply chain)",
                "Harta tak berwujud (intangible assets) yang dimiliki grup usaha",
                "Aktivitas pembiayaan atau finansial intra-grup",
                "Laporan keuangan konsolidasian grup usaha",
            ),
            kapanWajib = "Jika omzet konsolidasi grup > Rp50 M DAN transaksi afiliasi > Rp20 M.",
        ),
        DokumenInfo(
            kode = "LF",
            nama = "Local File",
            tujuan = "Dokumen berisi informasi spesifik dan mendetail mengenai transaksi yang dilakukan entitas " +
                "lokal di Indonesia dengan pihak afiliasinya. Membuktikan transaksi lokal sudah wajar dan tidak " +
                "merugikan penerimaan pajak negara tempat entitas berada.",
            isiUtama = listOf(
                "Profil bisnis entitas lokal, manajemen, dan strategi usaha",
                "Rincian transaksi afiliasi lokal (nilai, jenis transaksi, pihak yang terlibat)",
                "Analisis fungsional, risiko, dan aset (FAR Analysis) entitas lokal",
                "Pemilihan metode transfer pricing yang paling sesuai",
                "Analisis kesebandingan dengan data pembanding (benchmark) pihak independen",
            ),
            kapanWajib = "Jika omzet konsolidasi grup > Rp50 M DAN transaksi afiliasi > Rp20 M.",
        ),
        DokumenInfo(
            kode = "CbCR",
            nama = "Country-by-Country Report (CbCR)",
            tujuan = "Laporan per negara untuk grup multinasional besar, termasuk notifikasi status kewajiban pelaporan.",
            isiUtama = listOf(
                "Alokasi pendapatan, laba, pajak, dan kegiatan usaha per negara",
                "Disertai notifikasi CbCR yang disampaikan elektronik",
            ),
            kapanWajib = "Jika omzet konsolidasi grup > Rp11 T.",
        ),
    )

    val lampiranSpt = listOf(
        "3A-1" to "Lampiran rincian transaksi afiliasi dengan pihak yang memiliki hubungan istimewa",
        "3A-2" to "Lampiran rincian transaksi afiliasi dengan pihak yang tidak memiliki hubungan istimewa",
        "3B" to "Lampiran daftar transaksi yang dipengaruhi hubungan istimewa (transfer pricing)",
    )

    val notifikasiCbcr = "Notifikasi Country-by-Country Reporting (CbCR) adalah pemberitahuan wajib pajak " +
        "mengenai status kewajiban pelaporan CbCR grup multinasional. Notifikasi ini harus disampaikan " +
        "secara elektronik paling lama 12 bulan setelah akhir tahun pajak."

    // ---------- Sanksi ----------
    data class SanksiInfo(val nama: String, val keterangan: String)

    val sanksi = listOf(
        SanksiInfo(
            "Koreksi Pajak + Bunga 2%/bulan",
            "Koreksi atas PPh kurang bayar disertai bunga sebesar 2% per bulan atas kekurangan bayar.",
        ),
        SanksiInfo(
            "Denda administrasi 25%",
            "Denda administrasi sebesar 25% dari PPh kurang bayar apabila tidak patuh.",
        ),
        SanksiInfo(
            "Tidak bisa pakai P3B / Tax Treaty",
            "Hak memanfaatkan Persetujuan Penghindaran Pajak Berganda (P3B) dicabut apabila transaksi dianggap tidak substantif.",
        ),
    )

    // ---------- Kesimpulan / tabel keputusan ----------
    data class KondisiPerusahaan(
        val kondisi: String,
        val wajibTp: String,
        val wajibDokumen: String,
    )

    val kesimpulan = listOf(
        KondisiPerusahaan(
            "Multinasional: induk/anak perusahaan di luar negeri",
            "Ya - prinsip kewajaran (ALP)",
            "Ya, jika melewati threshold (Rp50 M & Rp20 M; CbCR Rp11 T)",
        ),
        KondisiPerusahaan(
            "Holding & anak sama-sama di Indonesia",
            "Ya - prinsip kewajaran (ALP)",
            "Ya, jika melewati threshold (Rp50 M & Rp20 M)",
        ),
        KondisiPerusahaan(
            "Punya cabang/SKP di luar negeri",
            "Ya - prinsip kewajaran (ALP)",
            "Ya, jika melewati threshold (Rp50 M & Rp20 M)",
        ),
        KondisiPerusahaan(
            "Transaksi berelasi ada, TAPI di bawah threshold",
            "Ya - prinsip kewajaran tetap berlaku",
            "Tidak wajib formal, tapi DJP bisa koreksi harga tidak wajar",
        ),
        KondisiPerusahaan(
            "Tidak ada transaksi dengan pihak berelasi",
            "Tidak",
            "Tidak",
        ),
    )

    // ---------- Struktur untuk pencarian ----------
    data class MateriItem(
        val id: String,
        val section: String,
        val title: String,
        val body: String,
        val keywords: List<String>,
        val route: String,
    )

    val allSearchItems: List<MateriItem> = buildList {
        add(
            MateriItem(
                "M-001", "Beranda", "Apa itu Transfer Pricing?",
                pengertianTP, listOf("transfer", "pricing", "hubungan istimewa", "afiliasi", "wp badan"), Routes.HOME,
            )
        )
        add(
            MateriItem(
                "M-002", "Beranda", "Prinsip Kewajaran & Kelaziman Usaha",
                prinsipKewajaran, listOf("kewajaran", "kelaziman", "arm", "length", "harga", "pihak ketiga"), Routes.HOME,
            )
        )
        add(
            MateriItem(
                "M-003", "Beranda", "Dasar Hukum",
                "PMK 172/2023 dan PER-22/PJ/2023.", listOf("pmk", "172", "per", "22", "dasar hukum", "aturan"), Routes.HOME,
            )
        )
        add(
            MateriItem(
                "M-004", "Kriteria Wajib TP", "Perusahaan Multinasional",
                "Punya induk/anak perusahaan di luar negeri. Contoh: holding di Singapore punya anak PT di Indonesia.",
                listOf("multinasional", "asing", "luar negeri", "holding", "induk"), Routes.KRITERIA,
            )
        )
        add(
            MateriItem(
                "M-005", "Kriteria Wajib TP", "Holding & anak di Indonesia",
                "Contoh: PT A pegang 80% saham PT B, lalu PT A tagih management fee ke PT B.",
                listOf("holding", "anak", "domestik", "management fee", "80"), Routes.KRITERIA,
            )
        )
        add(
            MateriItem(
                "M-006", "Kriteria Wajib TP", "Cabang / SKP di luar negeri",
                "Kantor pusat di Indonesia bertransaksi dengan cabang di Malaysia, Singapura, dll.",
                listOf("cabang", "skp", "kantor", "but", "tempat usaha tetap"), Routes.KRITERIA,
            )
        )
        add(
            MateriItem(
                "M-007", "Kriteria Wajib TP", "Catatan: prinsip kewajaran tetap wajib",
                catatanKriteria, listOf("threshold", "koreksi", "wajar", "djp", "catatan"), Routes.KRITERIA,
            )
        )
        add(
            MateriItem(
                "M-008", "Kalkulator", "Threshold Master File & Local File",
                "Wajib Master File + Local File jika omzet grup > Rp50 M DAN transaksi afiliasi > Rp20 M.",
                listOf("kalkulator", "threshold", "master", "local", "50", "miliar", "20"), Routes.KALKULATOR,
            )
        )
        add(
            MateriItem(
                "M-009", "Kalkulator", "Threshold CbCR",
                "CbCR dan notifikasi wajib jika omzet konsolidasi grup > Rp11 T.",
                listOf("cbcr", "threshold", "11", "triliun", "lapor"), Routes.KALKULATOR,
            )
        )
        add(
            MateriItem(
                "M-010", "Pihak Berelasi", "Kepemilikan saham >= 25%",
                "Satu pihak punya saham >= 25% di pihak lain (pengendalian).",
                listOf("berelasi", "hubungan istimewa", "saham", "25", "kepemilikan", "pengendalian"), Routes.BERELASI,
            )
        )
        add(
            MateriItem(
                "M-011", "Pihak Berelasi", "Keluarga sedarah/semenda derajat 2",
                "Ada keluarga sedarah/semenda sampai derajat 2, atau direksi/komisaris yang sama.",
                listOf("keluarga", "sedarah", "semenda", "derajat", "direksi", "komisaris"), Routes.BERELASI,
            )
        )
        add(
            MateriItem(
                "M-012", "Pihak Berelasi", "Ketergantungan keuangan/teknis",
                "Contoh: 1 supplier 90% barangnya ke kamu.",
                listOf("ketergantungan", "supplier", "90", "keuangan", "teknis", "usaha"), Routes.BERELASI,
            )
        )
        add(
            MateriItem(
                "M-013", "Jenis Transaksi", "Jual beli barang",
                "Anak perusahaan jual raw material ke holding.",
                listOf("jual beli", "barang", "raw material", "penjualan"), Routes.TRANSAKSI,
            )
        )
        add(
            MateriItem(
                "M-014", "Jenis Transaksi", "Jasa manajemen / management fee",
                "Holding tagih biaya ke anak perusahaan untuk HR, Finance, IT.",
                listOf("jasa", "management fee", "hr", "finance", "it", "administrasi"), Routes.TRANSAKSI,
            )
        )
        add(
            MateriItem(
                "M-015", "Jenis Transaksi", "Pinjam-meminjam / bunga",
                "Holding pinjamkan dana ke anak perusahaan.",
                listOf("pinjaman", "bunga", "utang", "financing", "dana"), Routes.TRANSAKSI,
            )
        )
        add(
            MateriItem(
                "M-016", "Jenis Transaksi", "Royalti / lisensi merek",
                "Bayar royalti ke induk di luar negeri.",
                listOf("royalti", "lisensi", "merek", "intangible", "hak cipta"), Routes.TRANSAKSI,
            )
        )
        add(
            MateriItem(
                "M-017", "Jenis Transaksi", "Sewa",
                "Sewa kantor, gudang, mesin antar perusahaan grup.",
                listOf("sewa", "kantor", "gudang", "mesin", "leasing"), Routes.TRANSAKSI,
            )
        )
        add(
            MateriItem(
                "M-018", "Dokumen TP", "Master File",
                "Gambaran grup usaha global: struktur kepemilikan, kegiatan usaha, intangible, pembiayaan, laporan keuangan konsolidasian.",
                listOf("master file", "grup", "global", "struktur", "organisasi"), Routes.DOKUMEN,
            )
        )
        add(
            MateriItem(
                "M-019", "Dokumen TP", "Local File",
                "Detail transaksi lokal + analisis kewajaran: profil bisnis, FAR analysis, metode TP, benchmark.",
                listOf("local file", "transaksi", "far", "benchmark", "analisis", "metode"), Routes.DOKUMEN,
            )
        )
        add(
            MateriItem(
                "M-020", "Dokumen TP", "CbCR",
                "Laporan per negara untuk grup besar.",
                listOf("cbcr", "per negara", "laporan", "country"), Routes.DOKUMEN,
            )
        )
        add(
            MateriItem(
                "M-021", "Dokumen TP", "Lampiran SPT 3A-1, 3A-2, 3B",
                "Formulir lampiran SPT Tahunan Badan terkait transaksi pihak berelasi.",
                listOf("spt", "3a-1", "3a-2", "3b", "lampiran", "tahunan"), Routes.DOKUMEN,
            )
        )
        add(
            MateriItem(
                "M-022", "Dokumen TP", "Notifikasi CbCR",
                notifikasiCbcr, listOf("notifikasi", "cbcr", "elektronik", "12 bulan", "pemberitahuan"), Routes.NOTIFIKASI,
            )
        )
        add(
            MateriItem(
                "M-023", "Sanksi", "Koreksi pajak + bunga 2%/bulan",
                "Koreksi atas PPh kurang bayar + bunga 2% per bulan. Denda administrasi 25% dari PPh kurang bayar.",
                listOf("sanksi", "koreksi", "bunga", "denda", "25", "pajak"), Routes.SANKSI,
            )
        )
        add(
            MateriItem(
                "M-024", "Sanksi", "Tidak bisa pakai P3B / tax treaty",
                "Hak memanfaatkan P3B dicabut bila transaksi dianggap tidak substantif.",
                listOf("p3b", "tax treaty", "double tax", "substansi"), Routes.SANKSI,
            )
        )
        add(
            MateriItem(
                "M-025", "Kesimpulan", "Tabel kondisi perusahaan",
                "Matriks: kondisi perusahaan -> wajib TP? wajib dokumen?",
                listOf("kesimpulan", "tabel", "kondisi", "wajib", "ringkasan"), Routes.KESIMPULAN,
            )
        )
        add(
            MateriItem(
                "M-026", "Notifikasi CbCR", "Pengingat 12 bulan setelah akhir tahun pajak",
                "Hitung mundur batas waktu notifikasi CbCR (12 bulan setelah 31 Desember tahun pajak).",
                listOf("pengingat", "reminder", "jatuh tempo", "12 bulan", "tahun pajak", "deadline"), Routes.NOTIFIKASI,
            )
        )
    }
}