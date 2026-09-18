package com.tpdoc.app.data.content

/**
 * Content tutorial/panduan in-app (STEP 5.B Tahap 2.6) — pure, JVM-testable.
 * Wordt getoond in de PanduanScreen (route "panduan"), bereikbaar via
 * Pengaturan → "Panduan Penggunaan".
 */
data class PanduanSection(
    val id: String,
    val judul: String,
    val body: String,
    val bullets: List<String> = emptyList(),
)

data class FaqEntry(
    val pertanyaan: String,
    val jawaban: String,
)

object PanduanContent {

    const val URL_OPENROUTER_KEYS = "https://openrouter.ai/keys"

    val sections: List<PanduanSection> = listOf(
        PanduanSection(
            "intro",
            "1. Pengenalan TP Doc",
            "Transfer Pricing (TP) is de prijs die gelieerde partijen onderling hanteren " +
                "bij transacties (goederen, diensten, leningen, royalti). In Indonesië geldt het " +
                "Principe van Gewaarlijke en Marktconforme zaken (Arm's Length) en de documentatieplicht " +
                "volgens PMK 172/2023 en PER-22/PJ/2023.",
            listOf(
                "Deze app helpt de documentatieplicht in kaart te brengen: bedrijven, hiërarchie, thresholds, checklist en analyse.",
                "Alle data blijft 100% lokaal op uw toestel.",
            ),
        ),
        PanduanSection(
            "tambah",
            "2. Cara Menambah Perusahaan",
            "Onderin het startscheen staat de tab Bedrijven. Druk op de knop + (rechtsonder) om een bedrijf toe te voegen.",
            listOf(
                "Nama PT * en Tahun Pajak * zijn verplicht.",
                "NPWP is optioneel; formaat bijv. 01.234.567.8-901.000.",
                "Kies een Status: induk (moeder), anak (kind), cucu (kleinkind) of cabang (filiaal).",
                "Druk op Simpan Perusahaan — het bedrijf verschijnt direct in de lijst.",
            ),
        ),
        PanduanSection(
            "hierarki",
            "3. Cara Input Hierarki Induk-Anak",
            "Een hiërarchie voer je in door het kindbedrijf de juiste status te geven en in " +
                "'Induk Perusahaan' de moeder aan te wijzen.",
            listOf(
                "Voorbeeld: PT Nusantara Holding (induk) → PT Nusantara Jaya (anak, inductie Holding) → PT Nusantara Internasionaal (cucu, inductie Jaya).",
                "Het detailscherm toont het organigram met kinderen eronder.",
                "Uitproberen? Gebruik 'Muat Data Contoh' in Instellingen — dit laadt deze hiërarchie meteen.",
            ),
        ),
        PanduanSection(
            "kalkulator",
            "4. Cara Pakai Kalkulator Threshold",
            "De calculator toont of de formaliteitsdocumenten (Master File, Local File, CbCR) verplicht zijn.",
            listOf(
                "Master File + Local File: totale groepomzet > Rp50 M EN gelieerde transacties > Rp20 M.",
                "CbCR: geconsolideerde groepomzet > Rp11 T (grote multinationale groepen).",
                "Grenzen zijn strikt 'meer dan' — exact gelijk aan de grens is nog niet formeel verplicht.",
            ),
        ),
        PanduanSection(
            "apikey",
            "5. Cara Setting API Key OpenRouter",
            "Voor AI-analyse is een OpenRouter API key nodig. Maak gratis een account en genereer een key.",
            listOf(
                "Ga naar " + URL_OPENROUTER_KEYS en genereer een key (formaat sk-or-v1-...).",
                "Open Instellingen → API Key OpenRouter en plak de key → Simpan.",
                "Druk op Test om de verbinding te controleren. Refresh daarna het modellijst.",
                "De key wordt versleuteld bewaard (EncryptedSharedPreferences).",
            ),
        ),
        PanduanSection(
            "ai",
            "6. Cara Analisis dengan AI",
            "Open een bedrijf → druk op 'Analisis TP Doc' → tab AI.",
            listOf(
                "Er verschijnt een kosten-dialog: kies componenten (profiel, kewajaran, risico, sanksi, enz.).",
                "De schatting toont tokens en USD/Rp-kosten vóór verzending.",
                "Alleen geselecteerde componenten worden verzonden; daarna verschijnt het resultaat naast de non-AI-uitslag.",
                "Waarschuwing: bedrijfsgegevens worden naar OpenRouter (derde partij) gestuurd.",
            ),
        ),
        PanduanSection(
            "export",
            "7. Cara Export CSV/PDF & Share",
            "Op de bedrijvenlijst (menu ⋮) en het dashboard staan exportknoppen.",
            listOf(
                "Export CSV: separator ; met UTF-8 BOM (Excel Indonesië vriendelijk).",
                "Export PDF: A4-overzicht van de groep of van één bedrijf (detailscherm).",
                "Share (deel ikoon): CSV via Android delen (WhatsApp, e-mail, bestanden).",
                "Backup JSON: volledige export van alle bedrijven.",
            ),
        ),
        PanduanSection(
            "backup",
            "8. Cara Backup/Restore",
            "Data is lokaal — maak regelmatig een backup zodat u niets verliest als het toestel wegvalt.",
            listOf(
                "Instellingen → Backup & Restore → Backup (JSON): sla een bestand op (bv. Google Drive).",
                "Restore (JSON): kies een backupbestand. Er volgt ALTIJD een bevestigingsdialoog voordat bestaande data wordt overschreven.",
            ),
        ),
        PanduanSection(
            "faq",
            "9. FAQ — Veelgestelde vragen",
            "Antwoorden op veelgestelde vragen vindt u hieronder.",
            listOf(
                "Zie het FAQ-gedeelte onderaan deze pagina (5 t/m 10 vragen).",
            ),
        ),
    )

    val faq: List<FaqEntry> = listOf(
        FaqEntry(
            "Kenapa analisis AI gagal?",
            "Meestal geen API key, ongeldige key, of geen internet. Controleer Instellingen → Test. " +
                "Zonder geldige key verschijnt 'Isi API key di Pengaturan' in de AI-tab.",
        ),
        FaqEntry(
            "Kenapa biya AI mahal?",
            "Kosten = tokens × prijs per model, verrekend met de wisselkoers in Instellingen. " +
                "Kies een goedkoper model of minder componenten in de kosten-dialog.",
        ),
        FaqEntry(
            "Kan de app zonder internet?",
            "Ja — alle content, checklist, calculator, bedrijfsbeheer en non-AI-analyse werken offline. " +
                "Alleen AI-analyse en het modellenoverzicht hebben internet nodig.",
        ),
        FaqEntry(
            "Wat gebeurt er met mijn data?",
            "Alles staat lokaal in de app-database en wordt alleen verzonden bij AI-analyse " +
                "(na de kosten-dialog) en bij delen (CSV). Maak backups via Instellingen.",
        ),
        FaqEntry(
            "Wat is 'data contoh' (dummy)?",
            "Drie voorbeeldbedrijven in een hiërarchie (2 belastingjaren) om de app te leren kennen. " +
                "Ze krijgen de vlag is_dummy en kunnen gewoon bewerkt/verwijderd worden; " +
                "'Hapus Data Contoh' ruimt ze in één keer op.",
        ),
        FaqEntry(
            "Mijn NPWP wordt afgekeurd?",
            "Voer alleen cijfers en scheidingstekens . of - in. Het veld is optioneel; bij leeg laten " +
                "wordt '-' opgeslagen.",
        ),
        FaqEntry(
            "Kan ik een bedrijf en de hierarchie verwijderen?",
            "Ja, via het prullenbak-icoon op de bedrijvenlijst. De bevestigingsdialoog waarschuwt dat de " +
                "hiërarchie mee wordt verwijderd.",
        ),
        FaqEntry(
            "Waar kan ik de tutorial opnieuw zien?",
            "Instellingen → 'Lihat Tutorial' opent het onboarding-scherm opnieuw; " +
                "Instellingen → 'Panduan Penggunaan' opent deze volledige handleiding.",
        ),
    )
}