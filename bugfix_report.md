# Bugfix Report — Tahap 2.6 · TPDocApp

Datum: 2026-09-19

## BUG 1 (KRITIS): Crash bij het opslaan van een nieuwe onderneming

**Repro (gemeld door gebruiker):** DaftarPerusahaan → + → formulier invullen → Simpan → app stopt onmiddellijk.

**Analyse oorzaak (code):**
1. De oude `FormPerusahaanViewModel.save()` had maar één veldvalidatie (naam) en schreef direct naar de database. Ongeldige invoer (leeg jaar, verkeerd NPWP-formaat, lange naam, ontbrekende velden) kon een onbestuurd pad naar constraint-/typefouten (of NPE) nemen.
2. Geen try-catch rond de database schrijfactie in de coroutine: elke DB-fout (constraint, schema, lock) zwol op naar een ongeladen crash in plaats van een beheersbare foutmelding.
3. Genormaliseerde invoer ontbrak: lege NPWP/negara en ongeldige `tahunPajak` gingen mogelijk door met null/garbage.

**Oplossing:**
- `validation/FormValidation.kt` (pure, JVM-testbaar): `validatePerusahaan()` — naam verplicht (max 120 karakters), NPWP optioneel maar formaat-gecontroleerd, status in toegestane set, jaar 2000–2100; `findDuplicateNpwp()` — duplicaat tastbaar vóór DB-schrijven.
- `FormPerusahaanViewModel`: validatie **vóór** elke DB-bewerking; als fouten → `fieldErrors` inline per veld; als duplicaat-NPWP → vriendelijke fout; elke DB-bewerking in geneste try-catch met `Log.e("FormPerusahaan", ...)` en een gebruikersvriendelijke boodschap (geen stack trace); UiState met `loading/saved/error/fieldErrors`.
- `FormPerusahaanScreen`: fouten inline onder het veld, verplicht/optioneel gemarkeerd (`*`), knop uitgeschakeld tijdens opslaan.

**Bestanden gewijzigd:**
- app/src/main/java/com/tpdoc/app/validation/FormValidation.kt (nieuw)
- app/src/main/java/com/tpdoc/app/ui/viewmodel/FormPerusahaanViewModel.kt (herschreven)
- app/src/main/java/com/tpdoc/app/ui/screens/FormPerusahaanScreen.kt
- app/src/main/java/com/tpdoc/app/data/room/Perusahaan.kt (const `EMPTY_NPWP`)
- app/src/main/java/com/tpdoc/app/data/room/PerusahaanDao.kt (+ `findByNpwp`)
- app/src/main/java/com/tpdoc/app/data/room/PerusahaanRepository.kt (+ `findByNpwp`)
- app/src/test/java/com/tpdoc/app/FormValidationTest.kt (nieuw, 13 tests)

---

## BUG 2: Error bij delen van CSV (FLAG_ACTIVITY_NEW_TASK)

**Foutmelding (gemeld):** "Gagal share CSV: Calling startActivity() from outside of an Activity context requires the FLAG_ACTIVITY_NEW_TASK flag."

**Oorzaak:** `ExportUtils.shareFile()` riep `context.startActivity(...)` aan met een chooser zonder `Intent.FLAG_ACTIVITY_NEW_TASK`; de context kwam uit de ViewModel (Application-context), niet uit een Activity-context.

**Oplossing:**
- Nieuw puur hulpontwerp `data/export/ShareIntent.kt`: `ShareIntent.build(uri, mime, title)` → `ShareSpec` met flags `FLAG_ACTIVITY_NEW_TASK (0x10000000) | FLAG_GRANT_READ_URI_PERMISSION (0x1)`; unit-testbaar.
- `ExportUtils.shareFile()`: intent bevat beide vlaggen via `spec.flags`; de via `Intent.createChooser(...)` gemaakte chooser krijgt ook `FLAG_ACTIVITY_NEW_TASK`.
- Context wordt nu bij voorkeur vanuit de Composable doorgegeven: `DetailPerusahaanScreen` en `DashboardScreen` vangen `LocalContext.current` op tijdens compositie en geven die door aan `ExportViewModel.sharePerusahaan(p, ctx)` / `shareCsv(ctx)` (preferred-pad uit de opdracht); de Application-context blijft als fallback.

**Bestanden gewijzigd:**
- app/src/main/java/com/tpdoc/app/data/export/ShareIntent.kt (nieuw)
- app/src/main/java/com/tpdoc/app/data/export/ExportUtils.kt
- app/src/main/java/com/tpdoc/app/ui/viewmodel/ExportViewModel.kt (context-param + Log.e)
- app/src/main/java/com/tpdoc/app/ui/screens/DetailPerusahaanScreen.kt
- app/src/main/java/com/tpdoc/app/ui/screens/DashboardScreen.kt
- app/src/test/java/com/tpdoc/app/ShareIntentTest.kt (nieuw, 5 tests)

---

## BUG 3: Menu "Analisis" niet zichtbaar in de UI

**Signaal:** gebruiker zoekt tevergeefs een menu "Analisis"; het analisisscherm bestaat, maar is alleen bereikbaar via een klein icoontje (Analytics) in de topbar van DetailPerusahaanScreen.

**Oorzaak:** geen duidelijk gelabeld toegangspunt.

**Oplossing:**
- `DetailPerusahaanScreen`: prominente, volle-breedte knop **"Analisis TP Doc"** (icoon + label) bovenaan de inhoud → opent `AnalisisScreen` (route `analisis/{perusahaanId}` was al geregistreerd in NavHost).
- `AnalisisScreen`: duidelijke **tab-toggle Non-AI / AI** (in plaats van beide secties onder elkaar); AI-tab toont een lege-staat-boodschap als er geen API key is.

**Bestanden gewijzigd:**
- app/src/main/java/com/tpdoc/app/ui/screens/DetailPerusahaanScreen.kt
- app/src/main/java/com/tpdoc/app/ui/screens/AnalisisScreen.kt
- app/src/main/java/com/tpdoc/app/ui/viewmodel/AIAnalisisViewModel.kt (+ `apiKeySet`)

---

## Algemene geleerde lessen (toegepast op alle formulieren)

1. **Elk formulier valideert** input vóór DB-schrijven (pure `FormValidation`); fouten inline, niets crasht.
2. **Elke DB-bewerking** in try-catch → Snackbar/Dialog met vriendelijke tekst + `Log.e` met duidelijke tag.
3. **Null-safety**: geen `!!` (scan = 0 hits); `?.` en default-waarden; intentie/argumenten op null gecontroleerd.
4. **Coroutine**: `viewModelScope` (geen GlobalScope); DB op Room-suspend-laag; zware I/O (bestanden, CSV/PDF) op `Dispatchers.IO`.
5. **UiState** (Loading/Success/Error/Empty) op de belangrijkste schermen; geen lege schermen tijdens laden.