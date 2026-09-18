# Requirements Tahap 2 — TPDocApp Multi-Perusahaan

Upgrade dari single-entity edukasi TP Doc menjadi aplikasi multi-perusahaan
dengan analisis dual mode. Single user, satu device, lokal saja.

## A. Manajemen Perusahaan
| ID | Requirement |
|----|-------------|
| R-101 | CRUD profil perusahaan dengan Room database |
| R-102 | Field: nama PT, NPWP, alamat, negara, status (induk/anak/cucu/cabang), parent_id, tahun pajak, logo (path lokal) |
| R-103 | Hierarki induk-anak-cucu: tampilkan dalam bagan org-chart dan tabel |
| R-104 | Multi-tahun pajak: satu perusahaan bisa punya data beberapa tahun |
| R-105 | Duplikat profil perusahaan: salin semua field kecuali NPWP dan nama (suffix " - Copy") |
| R-106 | Search + filter perusahaan by nama/NPWP/status/negara |
| R-107 | Dialog konfirmasi sebelum hapus perusahaan |
| R-108 | Desain UI untuk skala 100 perusahaan (search, filter, sorting) |

## B. Dashboard Grup
| ID | Requirement |
|----|-------------|
| R-201 | Ringkasan status TP Doc semua perusahaan: hijau (patuh), kuning (perlu tindakan), merah (belum patuh) |
| R-202 | Visualisasi bagan hierarki grup + tabel ringkasan |
| R-203 | Filter: tahun pajak, status, negara |
| R-204 | Legend/penjelasan warna |

## C. Analisis Per Perusahaan (Dual Mode)
| ID | Requirement |
|----|-------------|
| R-301 | Mode Non-AI (rule-based): kalkulator threshold, checklist berelasi, jenis transaksi, status dokumen, estimasi sanksi |
| R-302 | Mode AI: tombol "Analisis dengan AI" via OpenRouter |
| R-303 | Dialog konfirmasi sebelum analisis AI: checklist komponen, estimasi biaya, pilih semua/hapus semua |
| R-304 | Estimasi biaya: token input (panjang_karakter/4), token output (komponen×250), harga dari cache model |
| R-305 | Estimasi biaya ditampilkan dalam USD + Rupiah (kurs configurable) + disclaimer |
| R-306 | Biaya aktual ditampilkan setelah analisis (dari usage response OpenRouter) + selisih estimasi |
| R-307 | Prompt AI editable (DataStore) dengan placeholder: {nama_perusahaan}, {npwp}, {omzet}, {transaksi_afiliasi}, {komponen_analisis}, {data_perusahaan} |
| R-308 | Validasi prompt: harus mengandung {data_perusahaan} dan {komponen_analisis} |
| R-309 | Tombol: Simpan Prompt, Reset ke Default, Preview Prompt |
| R-310 | Template prompt default (analis TP Doc Indonesia, output JSON terstruktur) |
| R-311 | Hasil AI dan non-AI ditampilkan berdampingan (side-by-side/tab) |
| R-312 | Jika internet mati / API key kosong: tombol AI disabled + pesan |
| R-313 | Rate limit handling: HTTP 429 → pesan "coba lagi dalam X detik" + tombol Retry |
| R-314 | Timeout 60 detik. Jika lewat: "Analisis timeout, coba lagi" |
| R-315 | Retry logic: network error → retry otomatis 1 kali |
| R-316 | Riwayat analisis AI per perusahaan di Room (timestamp, model, komponen, hasil, biaya) |

## D. Pengaturan AI
| ID | Requirement |
|----|-------------|
| R-401 | Input API key OpenRouter (EncryptedSharedPreferences, masked) |
| R-402 | Validasi format API key (sk-or-v1-) dengan warning |
| R-403 | Tombol Test: panggil API OpenRouter → sukses/gagal + latency |
| R-404 | Dropdown model: fetch dari GET /api/v1/models, cache 24 jam |
| R-405 | Pagination model: 10 per halaman + Next/Prev + indikator halaman |
| R-406 | Search box model: filter by id/name (case-insensitive) dari cache |
| R-407 | Info tiap model: nama, id, harga input/output per 1jt, context length |
| R-408 | Simpan model terpilih sebagai default analisis |
| R-409 | Pengaturan kurs USD→IDR (default Rp16.000, editable) |
| R-410 | Disclaimer: data dikirim ke OpenRouter (pihak ketiga) |

## E. Export & Share
| ID | Requirement |
|----|-------------|
| R-501 | Export CSV: separator titik-koma (;), UTF-8 BOM |
| R-502 | Export PDF lengkap: ringkasan grup + detail per perusahaan + logo |
| R-503 | Upload logo per perusahaan (galeri/kamera, JPG/PNG, max 5MB) |
| R-504 | Share via Android Share Intent (Intent.ACTION_SEND) |

## F. Backup/Restore
| ID | Requirement |
|----|-------------|
| R-601 | Export seluruh data ke file JSON (SAF) |
| R-602 | Import dari file JSON untuk restore (SAF) |
| R-603 | Dialog konfirmasi sebelum restore (akan menimpa data existing) |
| R-604 | Peringatan: data 100% lokal, backup rutin |

## G. Keamanan
| ID | Requirement |
|----|-------------|
| R-701 | API key disimpan di EncryptedSharedPreferences (masked) |
| R-702 | Database tidak dienkripsi (data lokal, sesuai permintaan) |

## H. Unit Test & CI
| ID | Requirement |
|----|-------------|
| R-801 | Unit test CRUD perusahaan |
| R-802 | Unit test hierarki & duplikat |
| R-803 | Unit test kalkulasi per perusahaan |
| R-804 | Unit test agregasi dashboard |
| R-805 | Unit test parser response OpenRouter (mock) |
| R-806 | Unit test estimasi biaya (mock pricing) |
| R-807 | Unit test export CSV |
| R-808 | Unit test pagination + search model |
| R-809 | Unit test validasi API key format |
| R-810 | Unit test retry logic |
| R-811 | CI GitHub Actions hijau |
| R-812 | verification.md semua PASS |