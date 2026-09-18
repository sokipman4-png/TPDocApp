# Pending Items — Tahap 2.5

Items die bewust NIET binnen deze tahap worden afgerond, met motivering.

## 1. Verificatie op echte device / emulator (niet machine-testable in Termux)

Termux heeft geen Android SDK en geen emulator; de build/test-loop loopt via GitHub Actions.
De volgende flows compileren en zijn unit-getest op pure logica, maar de SAF/FileProvider
interactie kan alleen op een echte telefoon/emulator visueel bevestigd worden:

- [ ] Tab-transitie + tombol Dashboard: klik op elke bottom-nav tab en tombol Dashboard top-bar; geen crash
- [ ] Export CSV/PDF/Backup via CreateDocument (SAF save-dialog verschijnt; content klopt)
- [ ] Restore via OpenDocument: kies backup → dialog konfirmasi verschijnt VOORDAT data gewijzigd wordt → "Lanjut" importeert, "Batal" niet
- [ ] Share Laporan / Share Profil via Intent chooser (FileProvider authorities `${applicationId}.fileprovider`)
- [ ] Upload Logo: kies JPG/PNG ≤5MB → logo path saved; selecteer GIF/6MB → error melding
- [ ] Back-knop en Dashboard-knop op alle sub-screens (Kriteria etc. vanuit Home)

## 2. Infra items uit Tahap 2 (out of scope voor 2.5 — ongewijzigd)

- R-316: Riwayat analisis AI in Room — entity/infrastructuur klaar, UI-historie nog niet gebouwd
- R-805: Unit test parser OpenRouter (mock) — infrastructuur klaar, test zelf nog niet geschreven

## 3. Beperkingen / bewuste keuzes

- `lifecycle-runtime-compose` NIET toegevoegd: niets in de codebase gebruikt
  `collectAsStateWithLifecycle`, dus de dependency is overbodig (task zei "kalau perlu").
- Contract-launchers (CreateDocument/OpenDocument/GetContent) leven in de screens in plaats
  van in de ViewModel — standaard androidx.activity architectuur; ViewModel blijft pure logic +
  ContentResolver IO.
- Logopad wordt als string in `perusahaan.logoPath` bewaard (geen kopie van het bestand naar
  door de gebruiker gekozen locatie; pad verwijst naar cacheDir/logos).