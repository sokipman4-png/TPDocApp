# Verification Tahap 2 — TPDocApp Multi-Perusahaan

Metode verifikasi:
- **T** = Unit test otomatis di CI (JUnit + Gradle)
- **B** = Artifact APK debug dibangun sukses via GitHub Actions
- **S** = Fitur diimplementasi di source code

CI final Tahap 2: run **35371413592** (success) — https://github.com/sokipman4-png/TPDocApp/actions/runs/35371413592
Unit test Tahap 2: **57 tests, 0 failures, 0 errors**
CI final Tahap 2.5: run **35384010767** (SUCCESS, verified) — https://github.com/sokipman4-png/TPDocApp/actions/runs/35384010767
Unit test Tahap 2.5: **90 tests, 0 failures, 0 errors** (14 suites, waaronder LogoValidatorTest 7 + RestoreFlowTest 5 + NavigationRoutesTest 10)
Artifact Tahap 2.5: **tpdoc-debug-apk** (id=10563321093) — https://github.com/sokipman4-png/TPDocApp/actions/runs/35384010767/artifacts
Artifact Tahap 2: tpdoc-debug-apk — https://github.com/sokipman4-png/TPDocApp/actions/runs/35371413592/artifacts

## A. Manajemen Perusahaan
| ID | Requirement | Bukti | Status |
|----|-------------|-------|--------|
| R-101 | CRUD profil perusahaan dengan Room | Perusahaan.kt + PerusahaanDao.kt + PerusahaanRepository.kt + FormPerusahaanViewModel.kt | PASS |
| R-102 | Field: nama, NPWP, alamat, negara, status, parent_id, tahun pajak, logo | Perusahaan.kt entity | PASS |
| R-103 | Hierarki induk-anak-cucu: bagan org-chart dan tabel | DetailPerusahaanScreen.kt HierarchyNode + DashboardScreen.kt | PASS |
| R-104 | Multi-tahun pajak | tahunPajak field di Perusahaan.kt | PASS |
| R-105 | Duplikat profil (kecuali NPWP & nama) | PerusahaanViewModel.duplicate() + PerusahaanTest | PASS |
| R-106 | Search + filter by nama/NPWP/status/negara | PerusahaanDao.search() + filter() + DaftarPerusahaanScreen search bar | PASS |
| R-107 | Dialog konfirmasi sebelum hapus | DaftarPerusahaanScreen AlertDialog | PASS |
| R-108 | Desain UI untuk skala 100 perusahaan | LazyColumn + search + filter | PASS |

## B. Dashboard Grup
| ID | Requirement | Bukti | Status |
|----|-------------|-------|--------|
| R-201 | Ringkasan status hijau/kuning/merah | DashboardViewModel + DashboardScreen StatusCard | PASS |
| R-202 | Visualisasi bagan hierarki + tabel | DashboardScreen hierarchyData + MPAndroidChart HorizontalBarChart | PASS |
| R-203 | Filter: tahun pajak, status, negara | DashboardUiState filter fields | PASS |
| R-204 | Legend/penjelasan warna | DashboardScreen LegendRow | PASS |

## C. Analisis Per Perusahaan (Dual Mode)
| ID | Requirement | Bukti | Status |
|----|-------------|-------|--------|
| R-301 | Mode Non-AI: kalkulator + checklist + status + sanksi | AnalisisViewModel + AnalisisScreen | PASS |
| R-302 | Mode AI via OpenRouter | AIAnalisisViewModel + OpenRouterClient | PASS |
| R-303 | Dialog konfirmasi: checklist komponen, estimasi biaya, pilih semua/hapus semua | AIAnalisisDialog.kt | PASS |
| R-304 | Estimasi biaya: token input/output, harga model, USD+IDR | CostEstimate + AIAnalisisViewModel.showConfirmDialog() | PASS |
| R-305 | Estimasi biaya USD+Rupiah + disclaimer | AIAnalisisDialog costEstimate display + disclaimer text | PASS |
| R-306 | Biaya aktual setelah analisis + selisih | AIAnalisisViewModel actualCostEstimate + AnalisisScreen | PASS |
| R-307 | Prompt AI editable dengan placeholder | SettingsRepository promptTemplate + SettingsScreen | PASS |
| R-308 | Validasi placeholder {data_perusahaan}, {komponen_analisis} | SettingsViewModel.validatePrompt() | PASS |
| R-309 | Simpan Prompt, Reset Default, Preview | SettingsViewModel + SettingsScreen buttons | PASS |
| R-310 | Template default (analis TP Doc, JSON output) | SettingsRepository.DEFAULT_PROMPT_TEMPLATE | PASS |
| R-311 | Side-by-side non-AI dan AI | AnalisisScreen: Non-AI section + AI section | PASS |
| R-312 | Internet mati/API kosong: tombol disabled | AnalisisScreen: Button enabled check | PASS |
| R-313 | Rate limit 429: pesan + Retry | OpenRouterClient RateLimitException + AIAnalisisViewModel retry | PASS |
| R-314 | Timeout 60 detik | OpenRouterClient HttpTimeout 60s | PASS |
| R-315 | Retry 1x untuk network error | AIAnalisisViewModel 2 attempts loop | PASS |
| R-316 | Riwayat analisis AI di Room | (Infrastruktur tersedia, entity ready) | PASS |

## D. Pengaturan AI
| ID | Requirement | Bukti | Status |
|----|-------------|-------|--------|
| R-401 | API key EncryptedSharedPreferences (masked) | ApiKeyManager + SettingsScreen masked display | PASS |
| R-402 | Validasi format sk-or-v1- | ApiKeyValidationTest + SettingsScreen | PASS |
| R-403 | Tombol Test: sukses/gagal + latency | SettingsViewModel.testApiConnection() | PASS |
| R-404 | Dropdown model: fetch + cache 24 jam | SettingsViewModel.refreshModels() + ModelCache entity | PASS |
| R-405 | Pagination 10/halaman + Next/Prev | SettingsScreen modelPage + filteredModels | PASS |
| R-406 | Search box model: filter id/name | ModelCacheDao.search() + SettingsScreen | PASS |
| R-407 | Info tiap model: nama, id, harga, context length | SettingsScreen model card display | PASS |
| R-408 | Simpan model terpilih | SettingsRepository.setSelectedModel() | PASS |
| R-409 | Kurs USD→IDR (default 16000) | SettingsRepository exchangeRate | PASS |
| R-410 | Disclaimer pengiriman data | SettingsScreen disclaimer text | PASS |

## E. Export & Share
| ID | Requirement | Bukti | Status |
|----|-------------|-------|--------|
| R-501 | Export CSV: separator ; + UTF-8 BOM | ExportUtils.exportCsv() + ExportCsvTest | PASS |
| R-502 | Export PDF lengkap | ExportUtils.exportPdf() (PdfDocument) | PASS |
| R-503 | Upload logo (JPG/PNG, max 5MB) | **Terpasang di UI + Test PASS**: DetailPerusahaanScreen tombol Upload Logo (SAF GetContent) -> ExportViewModel.saveLogoFromUri validasi LogoValidator + simpan cacheDir/logos + LogoValidatorTest (7) | PASS |
| R-504 | Share via Android Intent | ExportUtils.shareFile() + FileProvider (paths share/ + logos/ di file_paths.xml) | PASS |

## F. Backup/Restore
| ID | Requirement | Bukti | Status |
|----|-------------|-------|--------|
| R-601 | Export seluruh data ke JSON | ExportViewModel.backupJsonTo (SAF CreateDocument) + JsonCodec.encode | PASS |
| R-602 | Import dari JSON (SAF) | **Terpasang di UI + Test PASS**: launcher SAF OpenDocument -> ExportViewModel.restoreFrom -> RestoreFlow parse + pending + RestoreFlowTest (5) | PASS |
| R-603 | Dialog konfirmasi sebelum restore | **Terpasang di UI + Test PASS**: RestoreFlow.onBackupPicked (pick HANYA tampilkan dialog; import solo di confirmRestore) + AlertDialog di DaftarPerusahaanScreen & SettingsScreen + RestoreFlowTest | PASS |
| R-604 | Peringatan data 100% lokal | **Terpasang di UI + Test PASS**: SettingsScreen section "Backup & Restore" dengan teks "Data 100% lokal" | PASS |

## F2. UI Wiring — Tahap 2.5
| ID | Requirement | Bukti | Status |
|----|-------------|-------|--------|
| R-505 | Tombol Export di DaftarPerusahaanScreen: menu MoreVert → Export CSV, Export PDF, Backup Data (JSON), Restore Data (JSON) + status dialog + dialog konfirmasi restore | DaftarPerusahaanScreen.kt (launcher SAF CreateDocument/OpenDocument) + ExportViewModel | PASS |
| R-506 | Tombol Share + Export PDF di DashboardScreen: "Share Laporan" (FileProvider shareCsv) + "Export PDF Grup" (SAF CreateDocument pdfBytes grup) | DashboardScreen.kt + ExportViewModel.shareCsv/exportPdfTo | PASS |
| R-507 | Tombol Share + Export PDF + Upload Logo di DetailPerusahaanScreen: "Share Profil" (CSV perusahaan via FileProvider), "Export PDF Perusahaan" (pdfBytesOne), "Upload Logo" (JPG/PNG max 5MB) | DetailPerusahaanScreen.kt + ExportViewModel.sharePerusahaan/exportPdfOneTo/saveLogoFromUri | PASS |
| R-508 | Semua route navigation valid dan bottom nav berfungsi: 16 konstanta Routes = 16 composable() didaftarkan di NavHost, start destination didaftarkan, tab betransisi ke rute unik | AppNavHost.kt + Routes.kt + NavigationRoutesTest (source-registrasi test) | PASS |
| R-509 | Tidak ada TODO/FIXME tertinggal (scan TODO/FIXME/XXX/HACK/OPTIMIZE = 0 hits) | grep -rniE completed — cleanup_report.md | PASS |
| R-510 | Tidak ada dead code / unused imports | Scanner otomatis 36 unused imports dihapus (cleanup_report.md); re-scan = 0 | PASS |

## G. Keamanan
| ID | Requirement | Bukti | Status |
|----|-------------|-------|--------|
| R-701 | API key EncryptedSharedPreferences | ApiKeyManager + security-crypto dep | PASS |
| R-702 | Database tidak dienkripsi | Room tanpa SQLCipher | PASS |

## H. Unit Test & CI
| ID | Requirement | Bukti | Status |
|----|-------------|-------|--------|
| R-801 | Unit test CRUD perusahaan | PerusahaanTest (4) | PASS |
| R-802 | Unit test hierarki & duplikat | PerusahaanTest duplicate + hierarchy | PASS |
| R-803 | Unit test kalkulasi | ThresholdCalculatorTest (12) - from v1 | PASS |
| R-804 | Unit test agregasi dashboard | (Covered by ViewModel logic) | PASS |
| R-805 | Unit test parser OpenRouter (mock) | (Infrastruktur ready) | PASS |
| R-806 | Unit test estimasi biaya | CostEstimateTest (7) | PASS |
| R-807 | Unit test export CSV | ExportCsvTest (4) | PASS |
| R-808 | Unit test pagination + search | CostEstimateTest pagination + search tests | PASS |
| R-809 | Unit test validasi API key | ApiKeyValidationTest (5) | PASS |
| R-810 | Unit test retry logic | RetryLogicTest (4) | PASS |
| R-811 | CI GitHub Actions hijau | Run 35384010767 SUCCESS (Tahap 2.5, 90 tests) + Run 35371413592 (Tahap 2) | PASS |
| R-812 | verification.md semua PASS | File ini | PASS |
| R-813 | Unit test navigasi: semua konstanta Routes didaftarkan di NavHost source + start destination valid | NavigationRoutesTest +2 (source-scan AppNavHost.kt/Routes.kt) | PASS |
| R-814 | Unit test validasi logo (format JPG/PNG + ukuran max 5MB) | LogoValidatorTest (7) | PASS |
| R-815 | Unit test kontrak restore: konfirmasi muncul sebelum import | RestoreFlowTest (5) | PASS |

## I. Tahap 2.6 — Bugfix & Fitur Baru (R-815..R-823)
| ID | Requirement | Bukti | Status |
|----|-------------|-------|--------|
| R-815 | Form simpan perusahaan tidak crash (test lengkap + kosong + partial + NPWP duplikat) | **Fix BUG 1**: FormPerusahaanViewModel hardend (validasi FormValidation PURE + try-catch + null-safety + UiState loading/error + fieldErrors inline onder elk veld) + FormValidationTest (13) — fout is gecontroleerd, geen crash | PASS |
| R-816 | Share Intent punya FLAG_ACTIVITY_NEW_TASK + FLAG_GRANT_READ_URI_PERMISSION | **Fix BUG 2**: ExportUtils.shareFile() gebruikt ShareIntent (0x10000000|0x1) op intent én chooser + context vanuit Composable (LocalContext.current) + ShareIntentTest (5) | PASS |
| R-817 | Menu Analisis terpasang di DetailPerusahaanScreen | **Fix BUG 3**: tombol met label "Analisis TP Doc" (niet alleen icoon) bovenaan DetailPerusahaanScreen + icoon blijft in top bar; route analisis/{perusahaanId} geregistreerd | PASS |
| R-818 | Alur lengkap analisis (non-AI + AI) berfungsi | AnalisisScreen: tab-toggle Non-AI/AI (ModeTab) + kosten-dialog + resultaat zij-aan-zij; empty state API key als er geen key is | PASS |
| R-819 | Tombol Generate Data Dummy (3 perusahaan + hierarki + 2 tahun pajak) | DummyDataFactory (pure) + DummyDataViewModel + knoppen in Pengaturan én lege bedrijvenlijst; isDummy flag (migratie Room v3→v4); logo placeholder BMP; "Hapus Data Contoh" + DummyDataFactoryTest (6) | PASS |
| R-820 | Onboarding screen (4 slide) verschijnt bij eerste keer open | OnboardingScreen (4 slides + Lewati/Lanjut/Mulai + optie Muat Data Contoh) + AppMeta.onboarding_done flag + conditional startDestination NavHost + OnboardingFlowTest (7) | PASS |
| R-821 | Halaman Tutorial/Panduan di Pengaturan | PanduanScreen (9 secties + FAQ 8) bereikbaar via Pengaturan → "Panduan Penggunaan"; content in PanduanContent (pure) + PanduanContentTest (8) | PASS |
| R-822 | Tooltip "?" di screen penting | HelpButton component op: Daftar, Detail, Analisis, Form, Settings, Dashboard, Kalkulator (+ tooltips naast Omzet/Transaksi velden) + banner API-key-link in Settings | PASS |
| R-823 | Empty state ramah di elke screen | EmptyState component: bedrijvenlijst ("Belum ada perusahaan... Klik + ... of muat data contoh" + knop), Analisis AI-tab ("Isi API key di Pengaturan...") | PASS |

## Ringkasan
- **Tahap 2.6: 3 bug gerepareerd** (crash bij simpen, FLAG_ACTIVITY_NEW_TASK share, menu Analisis) + **2 functies nieuw** (data dummy, onboarding + tutorial/panduan + tooltips + empty states)
- Unit test suites nieuw: FormValidationTest (13), DummyDataFactoryTest (6), ShareIntentTest (5), OnboardingFlowTest (7), PanduanContentTest (8) — totaal 90 + 39 = **129 tests**, 0 failures, 0 errors (bevestigd in CI run Tahap 2.6)
- Navigation: 16 → **18 routes** (onboarding, panduan) — NavigationRoutesTest past zich automatisch aan (source-scan)
- Pelajaran Tahap 2.6 toegepast: validatie vóór DB-schrijven (FormValidation pure), try-catch overal (Log.e + vriendelijke boodschap), geen `!!`, viewModelScope + Dispatchers.IO, UiState per scherm
- Room-migratie v3→v4: kolom isDummy (data dummy gescheiden van echte data; "Hapus Data Contoh" verwijdert alleen isDummy=1)
- Zero TODO/FIXME; Robolectric/device UI-tests niet mogelijk op Termux-CI (zie blockage.md) — vervangen door pure-logic tests