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
## Tahap 2.5 (2026-09-19) — Beslissingen extra

17. **Bouw-verificatie lokale onmogelijk**: geen Android SDK in Termux — de loop is altijd:
    code fix → push → GitHub Actions (testDebugUnitTest + assembleDebug) → lees fout → fix.
    Voor deze tahap zijn compile-fouten uit de vorige CI-run (35377072588) als ground truth gebruikt.

18. **SAF-contracten horen in screens, niet in ViewModel** (standaard androidx.activity architectuur):
    `rememberLauncherForActivityResult(CreateDocument/OpenDocument/GetContent)` in de composable;
    ViewModel ontvangt `android.net.Uri` en doet IO via `ContentResolver.openOutputStream/
    openInputStream/getType`. Dit vervangt de eerder gehallucineerde API zonder externe source te downloaden.

19. **`lifecycle-runtime-compose` NIET toegevoegd** aan build.gradle.kts — niets gebruikt
    `collectAsStateWithLifecycle`; de rest van de geëiste activity/navigation/viewmodel deps stonden al
    in (activity-compose 1.9.0, navigation-compose 2.7.7, viewmodel-compose 2.8.1 — versies naar boven
    bijgesteld t.o.v. de taaklijst, sesuai "sesuaikan dengan yang sudah ada").

20. **MoreVert-import in DaftarPerusahaanScreen is GEEN dead import** — het is het icon van het
    Export/Backup/Restore menu (claim in de taak was verouderd).

21. **Restore-flow**: memilih bestand zet alleen `showRestoreConfirm` + `pendingRestore` (pure
    `RestoreFlow`); import (replaceAll) kan uitsluitend via `confirmRestore()` die bij lege pending
    no-op is — zo staat de konfirmatie gegarandeerd vóór data-overschrijving. Getest door RestoreFlowTest.

22. **FileProvider**: `file_paths.xml` kreeg `cache-path share + logos` naast bestaande exports/backups
    (anders gooit `getUriForFile` "Failed to find configured root" bij Share/Logo).

23. **Unused-import scan**: automatische scanner (regex op import + woordgebruik, `by`-delegates
    uitgezonderd); 36 imports verwijderd, re-scan 0. TODO-scan met `\b`-word boundary: 0 hits
    (zonder boundary valse treffers door `toDouble*`).
