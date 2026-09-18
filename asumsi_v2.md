# Asumsi Tahap 2 — TPDocApp Multi-Perusahaan

Keputusan otonom yang diambil selama implementasi Tahap 2.

1. **Kurs USD→IDR**: default Rp16.000, disimpan di DataStore, bisa diubah user. Sumber: ditetapkan manual (user bisa update sendiri).

2. **Backup**: MANUAL saja (tombol export JSON). Tidak ada backup otomatis harian.

3. **Logo perusahaan**: dukung JPG dan PNG, ukuran maksimal 5 MB. Validasi saat upload, tolak file lain.

4. **Status kepatuhan (hijau/kuning/merah)**: 
   - Hijau: semua dokumen wajib (MF/LF/CbCR) sudah disiapkan
   - Kuning: ada yang perlu tindakan (threshold terpenuhi tapi belum lengkap)
   - Merah: threshold terpenuhi dan tidak ada dokumen yang disiapkan

5. **Hierarki**: parent_id nullable. NULL = induk (root). Hanya satu level root (satu grup usaha per instance aplikasi, tapi bisa banyak root untuk fleksibilitas).

6. **API key validation**: format minimal dicek "sk-or-v1-", tapi user bisa tetap simpan dengan konfirmasi.

7. **OpenRouter model cache**: disimpan di Room table `model_cache` dengan timestamp. Refresh jika > 24 jam atau user klik "Refresh".

8. **Prompt AI**: disimpan di DataStore sebagai string. Default template sesuai spesifikasi.

9. **CSV export**: separator titik-koma (;) + UTF-8 BOM untuk kompatibilitas Excel Indonesia.

10. **PDF**: gunakan Android PdfDocument API bawaan (ringan, tanpa dependency tambahan).

11. **HTTP client**: Ktor Client (ringan, Kotlin-native, tanpa dependency OkHttp tambahan).

12. **MPAndroidChart**: via AndroidView di Compose (sesuai requirement).

13. **Export JSON backup**: include semua tabel Room + DataStore settings.

14. **Migrasi Room**: v1 → v2 tambah tabel perusahaan (createTable), preserve checklist_items. Gunakan destructive migration fallback hanya jika diperlukan.

15. **minSdk tetap 24**, target 34, JDK 17.

16. **GitHub Actions build**: unit test + assembleDebug, upload artifact APK.