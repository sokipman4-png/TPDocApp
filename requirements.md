# Requirements — TPDocApp

Sumber: dokumen `TP Doc.docx` (Transfer Pricing Document Indonesia) + ruang lingkup misi.
Setiap ID digunakan pada `verification.md`.

## A. Konten Materi (dari dokumen)

| ID | Requirement | Sumber |
|----|-------------|--------|
| R-001 | Aplikasi menjelaskan definisi Transfer Pricing: transaksi dengan pihak yang memiliki hubungan istimewa/afiliasi | Dokumen baris 2 |
| R-002 | Menampilkan dasar hukum: PMK 172/2023 dan PER-22/PJ/2023 | Dokumen baris 3 |
| R-003 | Menjelaskan bahwa semua WP Badan dengan transaksi pihak berelasi wajib menerapkan TP (prinsip kewajaran) | Dokumen baris 4 |
| R-004 | Kriteria wajib: perusahaan multinasional dengan induk/anak perusahan di luar negeri (contoh: holding Singapore) | Dokumen baris 5-7 |
| R-005 | Kriteria wajib: holding & anak perusahaan sama-sama di Indonesia (contoh PT A 80% PT B + management fee) | Dokumen baris 8-9 |
| R-006 | Kriteria wajib: perusahaan dengan cabang/SKP di luar negeri | Dokumen baris 10 |
| R-007 | Catatan: walau di bawah threshold, prinsip kewajaran tetap wajib; DJP bisa koreksi harga tidak wajar | Dokumen baris 13 |
| R-008 | Kalkulator threshold menerima input omzet konsolidasi grup | Misi #3 |
| R-009 | Kalkulator threshold menerima input nilai transaksi afiliasi | Misi #3 |
| R-010 | Output wajib Master File + Local File jika omzet grup > Rp50 M DAN transaksi afiliasi > Rp20 M | Misi #3 |
| R-011 | Output wajib CbCR jika omzet grup > Rp11 T | Misi #3 + dokumen baris 35 |
| R-012 | Pihak berelasi: checklist kepemilikan saham >= 25% (pengendalian) | Dokumen baris 16 |
| R-013 | Pihak berelasi: keluarga sedarah/semenda sampai derajat 2 | Dokumen baris 17 |
| R-014 | Pihak berelasi: direksi/komisaris yang sama | Dokumen baris 17 |
| R-015 | Pihak berelasi: ketergantungan keuangan/teknis (contoh: 1 supplier 90% barangnya ke kamu) | Dokumen baris 18-19 |
| R-016 | Jenis transaksi: jual beli barang antar afiliasi (raw material ke holding) | Dokumen baris 22 |
| R-017 | Jenis transaksi: jasa manajemen / management fee (HR, Finance, IT) | Dokumen baris 23-24 |
| R-018 | Jenis transaksi: pinjam-meminjam / bunga intra-grup | Dokumen baris 25 |
| R-019 | Jenis transaksi: royalti / lisensi merek ke induk di LN | Dokumen baris 26 |
| R-020 | Jenis transaksi: sewa kantor/gudang/mesin antar perusahaan grup | Dokumen baris 27 |
| R-021 | Dokumen Local File: detail transaksi lokal + analisis kewajaran (profil bisnis, FAR, metode TP, benchmark) | Dokumen baris 32, 50-57 |
| R-022 | Dokumen Master File: gambaran grup global + isi utama (struktur kepemilikan, kegiatan usaha, intangible, pembiayaan, laporan keuangan konsolidasian) | Dokumen baris 33, 42-49 |
| R-023 | Dokumen CbCR: laporan per negara | Dokumen baris 34 |
| R-024 | Lampiran SPT Tahunan: form 3A-1, 3A-2, 3B | Dokumen baris 34 |
| R-025 | Notifikasi CbCR: wajib lapor elektronik jika grup > Rp11 T, paling lama 12 bulan setelah akhir tahun pajak | Dokumen baris 35-36 |
| R-026 | Sanksi: koreksi pajak + bunga 2%/bulan | Dokumen baris 38 |
| R-027 | Sanksi: denda administrasi 25% dari PPh kurang bayar | Dokumen baris 39 |
| R-028 | Sanksi: tidak bisa pakai P3B/Tax Treaty bila dianggap tidak substantif | Dokumen baris 40 |
| R-029 | Kesimpulan: tabel kondisi perusahaan -> wajib TP / wajib dokumen | Dokumen baris 41-57, misi #8 |
| R-030 | Notifikasi CbCR: pengingat berbasis 12 bulan setelah akhir tahun pajak (hitung mundur + status) | Misi #9 |
| R-031 | Pencarian/filter seluruh materi | Misi #10 |
| R-032 | Checklist interaktif dengan persistensi (Room) | Misi #10 |

## B. Kualitas & Teknis

| ID | Requirement | Sumber |
|----|-------------|--------|
| R-033 | Unit test kalkulator threshold (akurat per angka dokumen: 50M/20M/11T, batas > bukan >=) | Misi #5 |
| R-034 | Unit test logika checklist pihak berelasi | Misi #5 |
| R-035 | Unit test perhitungan notifikasi CbCR 12 bulan | Misi #5 |
| R-036 | Unit test pencarian/filter materi | Misi #5 |
| R-037 | Konten aplikasi offline penuh tanpa backend | Misi (Teknologi) |
| R-038 | minSdk 24, Kotlin + Jetpack Compose + Navigation Compose + ViewModel + Room/DataStore | Misi (Teknologi) |
| R-039 | APK debug dibangun via GitHub Actions (green run) | Strategi build |
| R-040 | Repo GitHub berisi source lengkap + workflow CI + README + requirements + verification + asumsi + rencana | Misi #7 |

## Ambang batas (angka resmi yang dipakai aplikasi)

- Master File + Local File: omzet konsolidasi grup **> Rp50 M** DAN transaksi afiliasi **> Rp20 M**
- CbCR + Notifikasi: omzet konsolidasi grup **> Rp11 T**
- Notifikasi CbCR: elektronik, **<= 12 bulan** setelah akhir tahun pajak (asumsi tahun buku = tahun kalender, berakhir 31 Desember)