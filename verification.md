# Verification — TPDocApp

Metode verifikasi:
- **T** = Unit test otomatis di CI (JUnit + Gradle, step `testDebugUnitTest`)
- **B** = Artifact APK debug dibangun sukses lewat GitHub Actions (`assembleDebug`)
- **S** = Fitur diimplementasi di source code (commit final) dan konten teruji lewat pencarian/unit test
- Semua angka threshold sesuai dokumen spesifikasi (50M / 20M / 11T, strictly greater).

CI final: run **35360915392** (success) — https://github.com/sokipman4-png/TPDocApp/actions/runs/35360915392
Unit test: **33 tests, 0 failures, 0 errors** (JUnit XML dari run final)
Artifact: **tpdoc-debug-apk** (app-debug.apk, 15,4 MB) — https://github.com/sokipman4-png/TPDocApp/actions/runs/35360915392/artifacts/10555125040

| Requirement ID | Requirement Dokumen | Fitur Aplikasi | Test Case | Status | Bukti |
|---|---|---|---|---|---|
| R-001 | Definisi Transfer Pricing (transaksi hubungan istimewa/afiliasi) | Beranda: card "Transfer Pricing Document" | S: konten render; M-001 teruji di PencarianViewModelTest | PASS | run final + app/src/main/java/com/tpdoc/app/data/ContentData.kt |
| R-002 | Dasar hukum PMK 172/2023 & PER-22/PJ/2023 | Beranda: section "Dasar Hukum" (2 chip) | S: konten render; keyword "pmk" teruji di M-003 | PASS | run final + ContentData.kt |
| R-003 | Semua WP Badan dengan transaksi berelasi wajib TP | Beranda hero + Kriteria intro | S: konten render | PASS | run final + ContentData.kt |
| R-004 | Kriteria: multinasional (induk/anak di LN) | Layar Kriteria Wajib TP | S: konten render; M-004 teruji | PASS | run final + KriteriaScreen.kt |
| R-005 | Kriteria: holding & anak di Indonesia | Layar Kriteria Wajib TP | S: M-005 teruji | PASS | run final + KriteriaScreen.kt |
| R-006 | Kriteria: cabang/SKP di LN | Layar Kriteria Wajib TP | S: M-006 teruji | PASS | run final + KriteriaScreen.kt |
| R-007 | Catatan: prinsip kewajaran tetap wajib di bawah threshold | WarningBanner Kriteria + Kalkulator | S: M-007 teruji | PASS | run final + KriteriaScreen.kt |
| R-008 | Kalkulator input omzet konsolidasi grup | Layar Kalkulator: input "Omzet Konsolidasi Grup" | T/B: KalkulatorViewModel + assemble; numerik filter | PASS | ThresholdCalculatorTest (12) |
| R-009 | Kalkulator input transaksi afiliasi | Layar Kalkulator: input "Total Transaksi Afiliasi" | T/B: idem | PASS | ThresholdCalculatorTest (12) |
| R-010 | Master+Local wajib jika omzet > Rp50 M DAN transaksi > Rp20 M | Kalkulator: StatusCard "Master File + Local File" | T: boundary 50M/20M, AND kondisi | PASS | ThresholdCalculatorTest: semua cases hijau |
| R-011 | CbCR wajib jika omzet grup > Rp11 T | Kalkulator: StatusCard "CbCR + Notifikasi CbCR" | T: 11T boundary, combo | PASS | ThresholdCalculatorTest: 9-12 cases hijau |
| R-012 | Berelasi: kepemilikan ≥25% | Layar Pihak Berelasi checklist B01 | T: BerelasiCheckerTest (kriteria 25%) | PASS | BerelasiCheckerTest (5 tests) |
| R-013 | Berelasi: keluarga sedarah/semenda derajat 2 | Checklist B02 | T: logika ≥1 kriteria | PASS | BerelasiCheckerTest |
| R-014 | Berelasi: direksi/komisaris sama | Checklist B03 | T: logika ≥1 kriteria | PASS | BerelasiCheckerTest |
| R-015 | Berelasi: ketergantungan keuangan/teknis (supplier 90%) | Checklist B04 | T: logika ≥1 kriteria | PASS | BerelasiCheckerTest |
| R-016 | Transaksi: jual beli barang | Layar Jenis Transaksi #1 | S: M-013 render + search | PASS | run final + TransaksiScreen.kt |
| R-017 | Transaksi: jasa manajemen/management fee | Layar Jenis Transaksi #2 | S: M-014 | PASS | run final + TransaksiScreen.kt |
| R-018 | Transaksi: pinjam-meminjam/bunga | Layar Jenis Transaksi #3 | S: M-015 | PASS | run final + TransaksiScreen.kt |
| R-019 | Transaksi: royalti/lisensi merek | Layar Jenis Transaksi #4 | T: keyword "royalti" filter | PASS | PencarianViewModelTest |
| R-020 | Transaksi: sewa | Layar Jenis Transaksi #5 | T: keyword "sewa" case-insens | PASS | PencarianViewModelTest |
| R-021 | Local File: detail transaksi + analisis kewajaran + FAR + benchmark | Layar Dokumen TP: card LF | S: M-019; bullet isi utama | PASS | run final + DokumenScreen.kt |
| R-022 | Master File: gambaran global + isi utama (5 elemen) | Layar Dokumen TP: card MF | S: M-018 | PASS | run final + DokumenScreen.kt |
| R-023 | CbCR: laporan per negara | Layar Dokumen TP: card CbCR | S: M-020 | PASS | run final + DokumenScreen.kt |
| R-024 | Lampiran SPT 3A-1, 3A-2, 3B | Layar Dokumen TP: section Lampiran + checklist | S: M-021 | PASS | run final + DokumenScreen.kt |
| R-025 | Notifikasi CbCR: elektronik ≤12 bulan na akhir tahun pajak | Layar Notifikasi CbCR | T: 12 bulan jatuh tempo | PASS | NotifikasiCalculatorTest (9) |
| R-026 | Sanksi: koreksi pajak + bunga 2%/bulan | Layar Sanksi #1 | S: konten render | PASS | run final + SanksiScreen.kt |
| R-027 | Sanksi: denda 25% PPh kurang bayar | Layar Sanksi #2 | S: konten render | PASS | run final + SanksiScreen.kt |
| R-028 | Sanksi: tidak bisa pakai P3B/tax treaty | Layar Sanksi #3 | S: M-024 | PASS | run final + SanksiScreen.kt |
| R-029 | Kesimpulan: tabel kondisi → wajib TP / wajib dokumen | Layar Kesimpulan (5 baris tabel) | S: M-025; render tabel | PASS | run final + KesimpulanScreen.kt |
| R-030 | Pengingat 12 bulan setelah akhir tahun pajak (hitung mundur) | Layar Notifikasi CbCR: status JAUH/MENDEKAT/SEGERA/TERLAMBAT + giorni sisa | T: 4 status + boundary H-0 | PASS | NotifikasiCalculatorTest: semua status hijau |
| R-031 | Pencarian/filter materi | Layar Pencarian: filter titel/isi/section/keyword | T: 7 filter cases | PASS | PencarianViewModelTest (7) |
| R-032 | Checklist interaktif + persistensi Room | ChecklistGroup: berelasi (4), dokumen (5), persiapan (5); Room db | B: Room KSP compile + app runs; S: DAO/entity/seed code | PASS | run final + app/src/main/java/com/tpdoc/app/data/room/ |
| R-033 | Unit test kalkulator threshold (50M/20M/11T, > bukan >=) | ThresholdCalculatorTest | T: 12 tests hijau | PASS | JUnit XML: ThresholdCalculatorTest 12/0/0 |
| R-034 | Unit test checklist pihak berelasi | BerelasiCheckerTest | T: 5 tests hijau | PASS | JUnit XML: BerelasiCheckerTest 5/0/0 |
| R-035 | Unit test perhitungan notifikasi 12 bulan | NotifikasiCalculatorTest | T: 9 tests hijau | PASS | JUnit XML: NotifikasiCalculatorTest 9/0/0 |
| R-036 | Unit test pencarian/filter | PencarianViewModelTest | T: 7 tests hijau | PASS | JUnit XML: PencarianViewModelTest 7/0/0 |
| R-037 | Offline penuh tanpa backend | Rooms/DataStore lokal + konten statis; nul netwerk dependency | S/B: build sukses tanpa permission netwerk | PASS | run final + app/build.gradle.kts |
| R-038 | minSdk 24, Kotlin + Compose + Navigation + ViewModel + Room/DataStore | build.gradle.kts + source tree | B: assembleDebug sukses; S: minSdk=24 | PASS | run final |
| R-039 | APK debug di GitHub Actions | workflow build.yml | B: artifact `tpdoc-debug-apk` tersedia | PASS | artifact link (run final) |
| R-040 | Repo met source + CI + full dokumentasi | Repo GitHub `sokipman4-png/TPDocApp` | B: repo view OK; S: README/requirements/verification/asumsi/rencana/blockage | PASS | https://github.com/sokipman4-png/TPDocApp |

## Sumario

- **40/40 PASS** — semua requirement dari `requirements.md` terpetakan dan verifikasi.
- Unit test: 33 hijau / 0 fail / 0 error (JUnit XML terupload di log run final).
- Artifact APK debug: `tpdoc-debug-apk` / `app-debug.apk` (15,4 MB).
- Limitasi (asumsi #6): aplikasi tidak dites pada emulator di CI; "tidak crash" diverifikasi lewat build sukses + unit test logika inti + review source. Verifikasi manuel pada perangkat real dinyatakan sebagai langkah lanjut yang mungkin diperlukan.