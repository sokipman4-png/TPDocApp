# Cleanup Report — Tahap 2.5 (UI wiring + navigation + cleanup)

Datum: 2026-09-19.
Scope: TPDocApp monorepo, module `app` (Kotlin/Compose, build via GitHub Actions).

---

## 1. TODO / FIXME scan

Metode: `grep -rniE "\b(TODO|FIXME|XXX|HACK|OPTIMIZE)\b"` di `app/src` (main + test + res).

**Resultaat: 0 hit.** Semua marker proyek-tech (TODO/FIXME/XXX/HACK/OPTIMIZE) tidak ada di source code.

Reload: sebuah scan naif `(?i)TODO` memunculkan 6 false positives dari `toDoubleOrNull()` /
`toDouble()` (prefisso "toDo" == "TODO" case-insensitive). Dengan word boundary `\bTODO\b`
false positives cero. (Hoe deze valkuil te vermeiden: gebruik altijd `\b...\b`.)

Tindakan per item: geen item gevonden → niets te selesaien/verwijderen/uitstellen.
Zie pending_items.md voor infra items die bewust blijven staan (R-316, R-805).

---

## 2. Dead code / unused imports verwijderd (36)

### Hallucinated API (root cause compile error, voormalige ExportViewModel)
Vervangen door Android-standaard API (ContentResolver + Compose activity launchers):

| Verwijderd (bestond niet) | Vervangen door |
|---|---|
| `androidx.unit.Unit` / `Unit.Default` | launcher SAF via `rememberLauncherForActivityResult` |
| `androidx.core.net.Uri` (ima-framework `Uri`) | `android.net.Uri` |
| `Application.startActivityForResult(...)` | Compose launcher in screen (CreateDocument/OpenDocument/GetContent) |
| `Uri.getContentHub()` / `getMimeType()` / `Uri.Mode.Append` | `ContentResolver.openOutputStream/openInputStream/getType` |
| `ActivityResultContracts.OpenDocument.withType()`, `CreateDocument.withName()` | contract constructor `CreateDocument(mimeType)` + `launch(nama)` / `launch(arrayOf(mimes))` |

Functies oude VM die vervangen zijn (signatuur gewijzigd van no-arg naar uri-based):
`exportCsv() → exportCsvTo(uri)`, `exportPdf() → exportPdfTo(uri)`,
`exportPdfOne(p) → exportPdfOneTo(uri, p)`, `backupJson() → backupJsonTo(uri)`,
`pickRestoreFile() → restoreFrom(uri)` (launcher), `pickLogo(id) → saveLogoFromUri(id, uri)`.

### Unused imports verwijderd (per bestand)
| Bestand | Import(s) |
|---|---|
| ui/screens/DaftarPerusahaanScreen.kt | `foundation.layout.width`, `material3.Button`, `material3.OutlinedButton`, `material3.TopAppBar`, `runtime.LaunchedEffect` |
| ui/screens/DashboardScreen.kt | `icons.filled.Business`, `ui.graphics.toArgb`, `data.room.Perusahaan` |
| ui/screens/DetailPerusahaanScreen.kt | `runtime.LaunchedEffect`, `ui.graphics.Color` |
| ui/screens/SettingsScreen.kt | `foundation.layout.width`, `icons.filled.Settings` |
| ui/screens/AnalisisScreen.kt | `foundation.layout.height`, `components.formatRupiah`, `flow.first` |
| ui/screens/KesimpulanScreen.kt | `foundation.layout.Column` |
| ui/screens/PencarianScreen.kt | `data.ContentData` |
| ui/screens/FormPerusahaanScreen.kt | `foundation.layout.Row`, `material3.Card`, `material3.CardDefaults`, `material3.DropdownMenu` |
| ui/components/AIAnalisisDialog.kt | `foundation.layout.Spacer`, `fillMaxWidth`, `height`, `runtime.getValue`, `mutableStateOf`, `remember`, `setValue` |
| ui/viewmodel/PerusahaanViewModel.kt | `flow.asStateFlow` |
| ui/viewmodel/SettingsViewModel.kt | `data.ai.OpenRouterModel`, `flow.SharingStarted`, `flow.stateIn` |
| ui/viewmodel/AnalisisViewModel.kt | `flow.SharingStarted`, `flow.stateIn` |
| ui/viewmodel/AIAnalisisViewModel.kt | `data.ai.ChatResponse`, `data.room.ModelCache` |

Re-scan na cleanup: **0 unused imports** (foundation-layout & runtime delegate imports die via
`by`-syntax gebruikt worden zijn als gebruikt behandeld).

Opmerking over de opdracht: `androidx.compose.material.icons.filled.MoreVert` in
DaftarPerusahaanScreen is **GEEN dead import** — hij wordt gebruikt als icon van het
menu ⋮ (Export/Backup/Restore). Niet verwijderd.

---

## 3. Compile errors gefixt (try logic → fix loop)

| Fout (CI run 35377072588) | Fix |
|---|---|
| ExportViewModel hallucinated API (16+ errors) | Volledige rewrite (zie boven) |
| JsonCodec.kt:62-63 `Double?.toLong()/.toInt()` | safe-call: `?.value?.toLong() ?: def` |
| DashboardScreen.kt:72,94 experimental Material3 API | `@OptIn(ExperimentalMaterial3Api::class)` toegevoegd |
| DetailPerusahaanScreen.kt:91,94 `Perusahaan?` i.p.v. `Perusahaan` | null-guard + launcher-based call |

---

## 4. File paths die aangepast zijn

| Bestand | Wijziging |
|---|---|
| app/src/main/java/com/tpdoc/app/ui/viewmodel/ExportViewModel.kt | rewrite: SAF via uri-launchers + ContentResolver, RestoreFlow (pure state machine), saveLogoFromUri; geen framework hallucination |
| app/src/main/java/com/tpdoc/app/ui/screens/DaftarPerusahaanScreen.kt | 4 launchers (CSV/PDF/JSON/OpenDocument) + menu acties ge-update + 5 unused imports |
| app/src/main/java/com/tpdoc/app/ui/screens/DashboardScreen.kt | PDF launcher + @OptIn + 3 unused imports |
| app/src/main/java/com/tpdoc/app/ui/screens/DetailPerusahaanScreen.kt | PDF+logo launchers + null-guards + 2 unused imports |
| app/src/main/java/com/tpdoc/app/ui/screens/SettingsScreen.kt | backup/restore launchers + 2 unused imports |
| app/src/main/res/xml/file_paths.xml | +cache-path `share/` en `logos/` (FileProvider root voor Share & Upload Logo) |
| app/src/main/java/com/tpdoc/app/data/export/JsonCodec.kt | nullable `value?.toLong()/.toInt()` fix (lines 62-63) |
| app/src/test/java/com/tpdoc/app/NavigationRoutesTest.kt | +2 tests (source registratie NavHost ↔ Routes, start destination) |
| app/src/test/java/com/tpdoc/app/LogoValidatorTest.kt | nieuw (7 tests) |
| app/src/test/java/com/tpdoc/app/RestoreFlowTest.kt | nieuw (5 tests) |
| app/src/test/java/com/tpdoc/app/JsonCodecTest.kt | test fix: `json string escaping` stuurde een string naar decode() die alleen `[{...}]`-objecten accepteert → nu ingebed als `"nama"`-waarde (test was al gebroken in commit 572aaa5 maar compileerde nooit) |
| verification_v2.md, cleanup_report.md, pending_items.md, blockage.md, asumsi_v2.md | documentatie |
| 9 bestanden overig | alleen import-verwijdering (zie tabel hierboven) |

## 5. Verificatie eindstatus

- `./gradlew :app:testDebugUnitTest` — 67 tests verwacht (na CI)
- `./gradlew :app:assembleDebug` — verwacht sukses (na CI)
- CI runnummer + artifact link: zie verification_v2.md (update na groene run)