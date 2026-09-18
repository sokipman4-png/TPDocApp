# TPDocApp — Transfer Pricing Document (Indonesia)

Aplikasi Android offline sebagai alat bantu edukasi, checklist, kalkulator threshold, dan
panduan kepatuhan **TP Doc** untuk Wajib Pajak (WP) Badan di Indonesia, berdasarkan
dokumen *TP Doc.docx* (aturannya: **PMK 172/2023** dan **PER-22/PJ/2023**).

> Disclaimer: Aplikasi ini adalah alat bantu edukasi; bukan nasihat perpajakan formal.
> Keputusan kepatuhan final tetap mengacu pada peraturan DJP yang berlaku.

## Fitur

| Fitur | Keterangan |
|-------|------------|
| Beranda | Ringkasan TP Doc, prinsip kewajaran (ALP), dasar hukum |
| Kriteria Wajib | Multinasional, holding & anak, cabang/SKP di luar negeri |
| Kalkulator Threshold | Master File + Local File: omzet grup > Rp50 M **DAN** transaksi afiliasi > Rp20 M; CbCR: omzet grup > Rp11 T |
| Pihak Berelasi | Checklist interaktif: kepemilikan ≥25%, keluarga derajat 2, direksi/komisaris sama, ketergantungan usaha (persisten via Room) |
| Jenis Transaksi | Jual beli, jasa manajemen, pinjam-meminjam/bunga, royalti/lisensi, sewa |
| Dokumen TP | Master File, Local File, CbCR, lampiran SPT 3A-1 / 3A-2 / 3B + checklist persiapan |
| Sanksi | Koreksi pajak + bunga 2%/bulan, denda 25%, kehilangan hak P3B/tax treaty |
| Kesimpulan | Tabel kondisi perusahaan → wajib TP? wajib dokumen? |
| Notifikasi CbCR | Pengingat hitung mundur: maksimal 12 bulan setelah akhir tahun pajak (31 Des), simpan tahun pajak via DataStore |
| Pencarian | Filter seluruh materi berdasarkan judul/isi/section/keyword |

## Teknologi

- Kotlin 2.0.20, Jetpack Compose (BOM 2024.06.00), Material 3
- Navigation Compose, ViewModel, Room 2.6.1 (checklist), DataStore (preferensi)
- minSdk 24, targetSdk 34, offline penuh tanpa backend

## Build

APK debug dibangun hanya lewat GitHub Actions (`./gradlew :app:testDebugUnitTest :app:assembleDebug`)
dan diunggah sebagai artifact `tpdoc-debug-apk` (`app-debug.apk`).

```bash
# lokal (butuh Android SDK)
./gradlew :app:assembleDebug
# di CI
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

## Struktur

```
TPDocApp/
├── app/src/main/java/com/tpdoc/app/
│   ├── calc/        # logika murni: ThresholdCalculator, NotifikasiCalculator, BerelasiChecker
│   ├── data/        # ContentData (basis materi), Room (checklist), DataStore
│   ├── navigation/  # NavHost + bottom bar
│   ├── ui/          # theme, components, screens, viewmodels
│   └── MainActivity.kt
├── app/src/test/    # unit test logika inti (threshold, berelasi, notifikasi, pencarian)
├── .github/workflows/build.yml
├── requirements.md  # R-001..R-040 (dari dokumen + misi)
├── verification.md  # pemetaan requirement → fitur → test → PASS/FAIL
├── asumsi.md        # keputusan otonom selama pengembangan
├── rencana.md       # rencana kerja
└── blockage.md      # catatan blocker (jika ada)
```

## Daftar berkas dokumentasi

- `requirements.md` — daftar requirement ber-ID unik (R-001..R-040)
- `verification.md` — tabel verifikasi: Requirement | Fitur | Test Case | Status | Bukti
- `asumsi.md` — asumsi & keputusan otonom
- `rencana.md` — rencana kerja
- `blockage.md` — blocker eksternal yang ditemui (kosong = tidak ada)