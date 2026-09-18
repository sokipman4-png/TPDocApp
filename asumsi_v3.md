# Asumsi Tahap 2.6 — TPDocApp Bugfix + Fitur Baru

Keputusan otonom yang diambil tijdens de implementatie (geen menselijke bevestiging gevraagd, per werkinstructie).

1. **BUG 1 root-cause**: De crash is niet reproduceerbaar op Termux (geen Android SDK/emulator), dus is de oorzaak aangepakt door constructie: alle onbestuurde paden (gebrek aan validatie, geen try-catch rond DB-schrijven, ongenormaliseerde invoer, null-gaten) zijn gesloten volgens de PELAJARAN PENTING. De MPWP-duplicaatcheck (voorheen kon dit een constraint-fout geven) loopt nu vóór het schrijven.

2. **"3 perusahaan met 2 tahun pajak"** vertaald naar 6 records: elk van de 3 bedrijven komt 2× voor (jaar 2024 en 2025) met de juiste parent-koppeling binnen dat jaar. Snackbar vermeldt beide aantallen. Adaptief: de belangrijkste gerapporteerde belofte "3 bedrijven + hiërarchie + 2 belastingjaren" blijft kloppen.

3. **Transaksi afiliasi dummy**: er is nog geen transactietabel in dit schema (alleen Perusahaan/ChecklistItem/ModelCache). Dummy-data vult daarom alle bedrijfsvelden plus hier ontbrekende transactiegegevens worden NIET opgeslagen (vastgelegd in feature_report). De analysetools lezen omzet/transacties uit invoervelden, dus dit blokkeert niets.

4. **Logo placeholder** = 16×16 BMP-kleurvlak (geen PNG-generator nodig); BMP is eenvoudig en pure testbaar. Mislukte logo-schrijfacties brengen de dummy-generatie NIET in gevaar.

5. **FLAG_ACTIVITY_NEW_TASK** (BUG 2) wordt op BOTH de share-intent én de chooser gezet (belt-and-suspenders) en de Activity-context wordt nu vanuit de Composable doorgegeven (LocalContext.current tijdens compositie — niet in onClick).

6. **Onboarding flag** wordt opgeslagen via SharedPreferences (niet DataStore) omdat de startroute VANUIT de compositie moet worden gekozen (sync-lees; DataStore is async flow). Andere niet-gevoelige app-meta kan hier later bij.

7. **"Lihat Tutorial"** herhaalt het onboarding-scherm (zonder te dwingen; de flag blijft staan). "Panduan Penggunaan" is de volledige handleiding.

8. **UI/Robolectric-testen** zijn niet mogelijk op deze CI (geen Android SDK op Termux, geen emulator in GH Actions voor Compose zonder Android-Test-Framework). In plaats daarvan is alle beslislogica in pure JVM-testbare modules ondergebracht (FormValidation, ShareIntent, DummyDataFactory, OnboardingFlow, PanduanContent) — zie blockage.md.

9. **Verplichte velden gemarkeerd** met `*` (naam en jaar); NPWP/alamat/land optioneel (leeg NPWP → `-`), jaarbereik 2000–2100.

10. **Restore uit backup** zet isDummy niet (backup-JSON bevat de kolom niet) — gerestaureerde data is dus nooit per ongeluk dummy-data.

11. **Tab-toggle** in AnalisisScreen geïmplementeerd met Material 3 `Button` + `ButtonDefaults` (geen SegmentedButton — niet beschikbaar in BOM 2024.06.00/material3 1.2.1).

12. **Foutmeldingen** worden afgekapt op 120 tekens en nieuwe regel (geen volledige stack traces naar de gebruiker); technische details gaan via `Log.e` met duidelijke tag.