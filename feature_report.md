# Feature Report — Tahap 2.6 · TPDocApp

## Data Contoh (Dummy) — STEP 4

### Doel
De gebruiker/client kan de app direct uitproberen zonder handmatige invoer.

### Spec
- Knop **"Muat Data Contoh (Dummy)"** in **Pengaturan** én in de lege bedrijvenlijst (empty state).
- **Bevestigingsdialog** vóór genereren: *"Data contoh zal worden toegevoegd (3 bedrijven, hiërarchie induk-anak-cucu, 2 belastingjaren). Lanjut? Data existing wordt niet verwijderd."*
- Na genereren **Snackbar**: *"Data contoh berhasil dibuat: 3 perusahaan (6 record, 2 tahun pajak)."*
- Knop **"Hapus Data Contoh"** (alleen zichtbaar als er dummy-data is) verwijdert ALLEEN `isDummy = true` rijen.

### Gegenereerde data (DummyDataFactory)
| Perusahaan (key) | Status | Land | Jaar |
|---|---|---|---|
| PT Nusantara Holding | induk (root) | Indonesia | 2024 + 2025 |
| PT Nusantara Jaya (anak van Holding) | anak | Indonesia | 2024 + 2025 |
| PT Nusantara Internasionaal (cucu van Jaya) | cucu | Singapura | 2024 + 2025 |

- Elke rij heeft een unieke NPWP, volledig ingevuld adres/land, status, `tahunPajak`, en `isDummy=true`.
- De hiërarchie wordt gelegd via parentKey → bij insert worden echte database-ids doorgegeven (ouders eerst).
- Logo placeholder: eenvoudig **16×16 BMP-kleurvlak** (kleur gehasht uit de naam) opgeslagen in `cacheDir/dummy_logos/`, gekoppeld via `logoPath`.
- Data is volledig bewerkbaar/verwijderbaar als normale bedrijven (isDummy is alleen een vlag).

### Scheiding dummy vs. echte data
- Room-migratie **v3→v4** voegt kolom `isDummy INTEGER NOT NULL DEFAULT 0` toe (`AppDatabase.kt`, `Perusahaan.isDummy`).
- `PerusahaanDao.deleteDummy()` / `getDummy()` beperken zich tot `isDummy = 1`.

### Bestanden
- app/src/main/java/com/tpdoc/app/data/dummy/DummyDataFactory.kt (pure factory + BMP-logo)
- app/src/main/java/com/tpdoc/app/ui/viewmodel/DummyDataViewModel.kt
- app/src/main/java/com/tpdoc/app/ui/screens/SettingsScreen.kt, DaftarPerusahaanScreen.kt, OnboardingScreen.kt (knop op slide 4)
- app/src/main/java/com/tpdoc/app/data/room/Perusahaan.kt, PerusahaanDao.kt, PerusahaanRepository.kt, AppDatabase.kt
- app/src/test/java/com/tpdoc/app/DummyDataFactoryTest.kt (6 tests)

---

## Onboarding (4 slides) — STEP 5.A

### Flow
1. Bij de **eerste start** (flag `onboarding_done` afwezig in `AppMeta`) is de startroute van de NavHost `onboarding`; daarna altijd `daftar_perusahaan`.
2. Slide 1: *"Selamat Datang di TP Doc Manager"* + illustratie + knoppen **Lewati / Lanjut**.
3. Slide 2: *"Kelola Banyak Perusahaan"* — multi-onderneming + hiërarchie.
4. Slide 3: *"Analisis TP Doc dengan atau tanpa AI"* — dual mode.
5. Slide 4: *"Siap Mulai"* — knop **"Mulai Sekarang"** + optie **"Muat Data Contoh"** (met bevestiging; na genereren direct door naar de app).
6. Na afronden: `AppMeta.setOnboardingDone()` (SharedPreferences) + navigatie naar de bedrijvenlijst.
7. Opnieuw te openen via **Pengaturan → "Lihat Tutorial"**.

### Bestanden
- app/src/main/java/com/tpdoc/app/ui/screens/OnboardingScreen.kt
- app/src/main/java/com/tpdoc/app/ui/viewmodel/OnboardingFlow.kt (pure state-machine)
- app/src/main/java/com/tpdoc/app/data/AppMeta.kt
- app/src/main/java/com/tpdoc/app/navigation/AppNavHost.kt, Routes.kt
- app/src/test/java/com/tpdoc/app/OnboardingFlowTest.kt (7 tests)

---

## Tutorial / Panduan in-app — STEP 5.B

### Locatie
**Pengaturan → "Panduan Penggunaan"** → route `panduan` (NavHost); scrollbare pagina.

### Inhoud (PanduanContent, 9 secties + 8 FAQ)
1. Pengenalan TP Doc
2. Cara Menambah Perusahaan (stap voor stap, verplicht/optioneel)
3. Cara Input Hierarki Induk-Anak
4. Cara Pakai Kalkulator Threshold
5. Cara Setting API Key OpenRouter (met link openrouter.ai/keys)
6. Cara Analisis dengan AI (incl. kosten-dialog)
7. Cara Export CSV/PDF & Share
8. Cara Backup/Restore
9. FAQ — veelgestelde vragen (8 vragen: waarom AI mislukt, waarom kost het geld, offline werking, data-privacy, dummy-data, NPWP, hiërarchie verwijderen, tutorial opnieuw bekijken)

### Bestanden
- app/src/main/java/com/tpdoc/app/data/content/PanduanContent.kt (pure content)
- app/src/main/java/com/tpdoc/app/ui/screens/PanduanScreen.kt
- app/src/main/java/com/tpdoc/app/navigation/AppNavHost.kt, Routes.kt
- app/src/test/java/com/tpdoc/app/PanduanContentTest.kt (8 tests)

---

## Help / Tooltip & Empty states — STEP 5.C/D/E

- **HelpButton ("?")** component (Common.kt) op: Daftar, Detail, Analisis, Form, Pengaturan, Dashboard, Kalkulator; plus tooltips naast de velden "Omzet Konsolidasi Grup" en "Total Transaksi Afiliasi" in de Kalkulator (uitleg over wat de drempelwaarden zijn).
- **Pengaturan AI**: banner *"Butuh API key dari openrouter.ai — klik di sini untuk daftar"* (opent de browser met Intent ACTION_VIEW + FLAG_ACTIVITY_NEW_TASK).
- **Empty states**: bedrijvenlijst (*"Belum ada perusahaan. Klik + voor toevoegen, of muat data contoh."* + knop), Analisis AI-tab (*"Isi API key di Pengaturan voor AI-analyse."*) — zie EmptyState component.

## Toetsbare pure logic (JVM, in CI)
| Suite | Aantal tests | Dekt |
|---|---|---|
| FormValidationTest | 13 | simpen lengkap/kosong/partial + NPWP-duplicaat (BUG 1) |
| ShareIntentTest | 5 | flags share-intent (BUG 2) |
| DummyDataFactoryTest | 6 | dummy-hiërarchie + isDummy + BMP-logo |
| OnboardingFlowTest | 7 | 4 slides flow + afronden |
| PanduanContentTest | 8 | 9 secties + FAQ 5–10 + link openrouter |