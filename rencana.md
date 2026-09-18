# Rencana Kerja — TPDocApp

Aplikasi Android edukasi & kepatuhan Transfer Pricing Document (TP Doc) Indonesia,
dibangun untuk WP Badan. Offline, Kotlin + Jetpack Compose, dibangun via GitHub Actions.

## Tahapan

1. [x] Baca & ekstrak `TP Doc.docx` -> `tpdoc_extracted.md` (57 baris konten).
2. [x] Susun `requirements.md` (R-001..R-040) dari isi dokumen + ruang lingkup.
3. [x] Buat `asumsi.md` untuk setiap keputusan yang diambil otonom.
4. [ ] Scaffold project `TPDocApp` (Gradle 8.7, AGP 8.5.2, Kotlin 2.0.20, Compose BOM 2024.06.00, Room 2.6.1, DataStore, minSdk 24).
5. [ ] Implementasi modul:
   - Home (ringkasan + dasar hukum PMK 172/2023 & PER-22/PJ/2023)
   - Kriteria Wajib TP (multinasional, afiliasi, cabang/SKP)
   - Kalkulator Threshold (Master+Local: omzet > Rp50 M DAN transaksi afiliasi > Rp20 M; CbCR: omzet > Rp11 T)
   - Pihak Berelasi (checklist interaktif + persistensi Room)
   - Jenis Transaksi (jual beli, jasa manajemen, pinjam-meminjam, royalti, sewa)
   - Dokumen TP (Master File, Local File, CbCR, Notifikasi CbCR, Lampiran 3A-1/3A-2/3B + checklist persiapan)
   - Sanksi (koreksi + bunga 2%/bulan, denda 25%, P3B)
   - Kesimpulan (tabel kondisi perusahaan)
   - Notifikasi CbCR (reminder 12 bulan setelah akhir tahun pajak, DataStore)
   - Pencarian/filter materi
6. [ ] Unit test: ThresholdCalculator, NotifikasiCalculator, BerelasiChecker, pencarian.
7. [ ] Bootstrap Gradle wrapper (tanpa gradle lokal), workflow GitHub Actions.
8. [ ] git init, commit per milestone, push ke GitHub, pantau run sampai hijau.
9. [ ] `verification.md`: petakan semua requirement -> PASS/FAIL + bukti.
10. [ ] Output akhir: URL repo, run, artifact APK + seluruh dokumen.

## Strategi build

- Build APK HANYA di GitHub Actions (Termux tanpa SDK).
- Jika workflow gagal: baca log, perbaiki, push ulang, iterasi sampai hijau.
- Blocker eksternal -> catat di `blockage.md`, kerjakan tugas lain, kembali lagi.

## Versi matrix (terverifikasi dari pengalaman build sebelumnya)

Gradle wrapper 8.7 | AGP 8.5.2 | Kotlin 2.0.20 | KSP 2.0.20-1.0.24 | Compose BOM 2024.06.00
Room 2.6.1 | Navigation Compose 2.7.7 | DataStore 1.1.1 | minSdk 24 | target/compileSdk 34 | JDK 17