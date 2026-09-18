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
| Perusahaan (v2) | CRUD multi-perusahaan + hierarki induk-anak-cucu + search/filter + duplikat + dashboard grup |
| Analisis TP Doc | Dual mode per bedrijf: **Non-AI** (kalkulator + checklist + sanksi) en **AI** (OpenRouter, met kosten-dialog) |
| Data Contoh | Laad 3 voorbeeldbedrijven in hiërarchie (2 belastingjaren) via Pengaturan of lege lijst; isDummy vlag; knop "Hapus Data Contoh" |
| Onboarding & Panduan | 4-slide intro bij eerste start; volledige handleiding + FAQ in Pengaturan → "Panduan Penggunaan" |

## Teknologi

- Kotlin 2.0.20, Jetpack Compose (BOM 2024.06.00), Material 3
- Navigation Compose, ViewModel, Room 2.6.1 (checklist), DataStore (preferensi)
- minSdk 24, targetSdk 34, offline penuh tanpa backend

## Install APK

1. Open een **succesvolle GitHub Actions run** van de `main`-branch.
2. Klik onderaan de run onder **Artifacts** op **tpdoc-debug-apk** → download `app-debug.apk`.
3. Zet het bestand op de telefoon (USB/Dir transfer of kabel) en open het om te installeren (zetz "onbekende bronnen" toe indien gevraagd).
4. Open de app; de **onboarding** (4 slides) verschijnt bij de eerste start.

## Cara Pakai (snelstart)

- **Eerste keer**: volg de onboarding; druk op "Muat Data Contoh" om meteen 3 voorbeeldbedrijven in een hiërarchie te laden (of + in de bedrijvenlijst voor eigen data).
- **Bedrijven**: tabl "Perusahaan" onderin → + (nieuw), klik een bedrijf voor detail (edit, logo, export PDF, share, analisis).
- **Analisis**: tik "Analisis TP Doc" → tab **Non-AI** (gratis, offline) of tab **AI** (vereist API key; kosten-dialog vóór verzenden).
- **Kalkulator**: toont of Master File/Local File/CbCR verplicht zijn; "?" naast de velden legt begrippen uit.
- **Tutorial**: Pengaturan → "Lihat Tutorial" (onboarding opnieuw) of "Panduan Penggunaan" (volledige handleiding + FAQ).
- **Data voorbeeld**: Pengaturan → "Muat Data Contoh (Dummy)"; opschonen via "Hapus Data Contoh" (verwijdert ALLEEN dummy-data).

## Cara Setting API Key OpenRouter

1. Maak een account op **https://openrouter.ai/keys** en genereer een key (`sk-or-v1-...`).
2. In de app: **Dashboard (uit de bedrijvenlijst: dashboard-icoon)** → **Pengaturan** (tandwiel) → **API Key OpenRouter**.
3. Plak de key → **Simpan** → **Test** om de verbinding te controleren.
4. Druk daarna op **Refresh Daftar Model** en kies een model. De key wordt **versleuteld** bewaard.
5. AI-analyse is dan beschikbaar in elk bedrijf → "Analisis TP Doc" → tab AI.

## Cara Backup/Restore

- **Backup**: op de bedrijvenlijst (menu ⋮) of in Pengaturan → **Backup (JSON)** — kies een save-locatie (bijv. Google Drive).
- **Restore**: **Restore (JSON)** en kies een backupbestand; er volgt **altijd** een bevestigingsdialoog voordat bestaande data wordt overschreven.
- Data staat 100% lokaal op het toestel; maak regelmatig een backup.

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
│   ├── validation/  # FormValidation (pure) — validatie vóór DB-schrijven
│   ├── data/        # ContentData, PanduanContent, DummyDataFactory, Room, DataStore, AppMeta
│   ├── navigation/  # NavHost + bottom bar (+ onboarding start)
│   ├── ui/          # theme, components (HelpButton/EmptyState), screens, viewmodels
│   └── MainActivity.kt
├── app/src/test/    # unit test logika inti (incl. FormValidationTest, DummyDataFactoryTest, ShareIntentTest, OnboardingFlowTest, PanduanContentTest)
├── .github/workflows/build.yml
├── requirements.md  # R-001..R-040 (dari dokumen + misi)
├── verification_v2.md # pemetaan requirement → fitur → test → PASS/FAIL
├── bugfix_report.md  # 3 bug Tahap 2.6: oorzaak + oplossing + gewijzigde bestanden
├── feature_report.md # data dummy + onboarding + tutorial (spec, flow, content)
├── asumsi.md / asumsi_v2.md / asumsi_v3.md  # keputusan otonom per tahap
├── rencana.md       # rencana kerja
└── blockage.md      # catatan blocker (indien aanwezig)
```

## Daftar berkas dokumentasi

- `requirements.md` — daftar requirement ber-ID unik (R-001..R-040)
- `verification.md` — tabel verifikasi: Requirement | Fitur | Test Case | Status | Bukti
- `asumsi.md` — asumsi & keputusan otonom
- `rencana.md` — rencana kerja
- `blockage.md` — blocker eksternal yang ditemui (kosong = tidak ada)