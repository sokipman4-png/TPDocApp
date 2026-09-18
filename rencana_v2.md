# Rencana Kerja Tahap 2 — TPDocApp Multi-Perusahaan

Aplikasi upgrade dari single-entity edukasi TP Doc menjadi multi-perusahaan
dengan analisis dual mode (non-AI + AI via OpenRouter).

## Milestone

### Milestone 1: Room migration + CRUD perusahaan + hierarki + duplikat + search/filter
- [ ] Entity `Perusahaan` (id, nama, npwp, alamat, negara, status, parentId, tahunPajak, logoPath)
- [ ] Room migration v1→v2 (tambah tabel perusahaan, preserve checklist lama)
- [ ] DAO `PerusahaanDao` (CRUD, search, filter, flow)
- [ ] Repository `PerusahaanRepository`
- [ ] ViewModel `PerusahaanViewModel` (CRUD, search, filter, sort, duplikat)
- [ ] UI: DaftarPerusahaanScreen + FormPerusahaanScreen + DetailPerusahaanScreen
- [ ] Hierarki: tampilkan bagan org-chart (sederhana di Compose) + tabel
- [ ] Duplikat: salin semua field kecuali NPWP & nama (tambah suffix " - Copy")
- [ ] Search/filter by nama, NPWP, status, negara
- [ ] Dialog konfirmasi sebelum hapus
- [ ] Desain UI untuk skala 100 perusahaan

### Milestone 2: Dashboard grup + chart + tabel + legend + filter
- [ ] DashboardScreen: ringkasan status TP Doc semua perusahaan (hijau/kuning/merah)
- [ ] Bagan hierarki grup (Compose Canvas/Column) + tabel ringkasan
- [ ] Filter: tahun pajak, status, negara
- [ ] Legend/penjelasan warna
- [ ] Integrasi dengan data perusahaan

### Milestone 3: Pengaturan AI (API key + model + kurs)
- [ ] EncryptedSharedPreferences untuk API key
- [ ] Input API key (masked, test, validasi format sk-or-v1-)
- [ ] Fetch daftar model dari OpenRouter (cache 24 jam di Room/DataStore)
- [ ] Dropdown model: pagination 10/halaman, search box, info harga & context
- [ ] Tombol Refresh daftar model
- [ ] Pengaturan kurs USD→IDR (default Rp16.000, editable)
- [ ] Disclaimer pengiriman data ke pihak ketiga

### Milestone 4: Analisis non-AI per perusahaan + riwayat
- [ ] Entity `AnalisisNonAI` (Room) + hasil analisis
- [ ] Kalkulator threshold per perusahaan
- [ ] Checklist pihak berelasi per perusahaan
- [ ] Jenis transaksi per perusahaan
- [ ] Status kewajiban dokumen (Master File, Local File, CbCR)
- [ ] Sanksi estimasi
- [ ] Riwayat analisis per perusahaan

### Milestone 5: Analisis AI + dialog konfirmasi biaya + prompt editable + side-by-side
- [ ] Entity `AnalisisAI` (Room) + `RiwayatAnalisis`
- [ ] Tombol "Analisis dengan AI" per perusahaan
- [ ] DIALOG KONFIRMASI: checklist komponen, estimasi biaya (USD+IDR), pilih semua/hapus semua
- [ ] Estimasi token input/output + biaya dari cache model pricing
- [ ] Prompt AI editable (DataStore) dengan placeholder {data_perusahaan}, {komponen_analisis}
- [ ] Validasi placeholder sebelum simpan
- [ ] Preview prompt
- [ ] Reset ke default
- [ ] HTTP client: Ktor/Retrofit untuk OpenRouter
- [ ] Rate limit (429) handling + timeout 60s + retry 1x
- [ ] Biaya aktual setelah analisis (dari usage response)
- [ ] Side-by-side display (non-AI vs AI)
- [ ] Riwayat analisis (timestamp, model, biaya)

### Milestone 6: Export CSV + PDF + logo + share + backup/restore
- [ ] Export CSV (separator ; + UTF-8 BOM)
- [ ] Export PDF lengkap (ringkasan grup + detail per perusahaan + logo)
- [ ] Upload logo (galeri/kamera, max 5MB, JPG/PNG)
- [ ] Share via Android Share Intent
- [ ] Backup seluruh data ke JSON (SAF)
- [ ] Restore dari JSON (dialog konfirmasi overwrite)
- [ ] Peringatan backup rutin

### Milestone 7: Unit test lengkap + CI hijau + verification.md
- [ ] Unit test CRUD perusahaan
- [ ] Unit test hierarki & duplikat
- [ ] Unit test kalkulasi per perusahaan
- [ ] Unit test agregasi dashboard
- [ ] Unit test parser response OpenRouter (mock)
- [ ] Unit test estimasi biaya (mock pricing)
- [ ] Unit test export CSV
- [ ] Unit test pagination + search model
- [ ] Unit test validasi API key format
- [ ] Unit test retry logic
- [ ] CI GitHub Actions hijau
- [ ] verification.md semua PASS