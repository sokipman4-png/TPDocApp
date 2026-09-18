# Asumsi & Keputusan Otonom

Semua asumsi di bawah diambil karena bekerja otonom tanpa supervisor; dicatat untuk transparansi.

1. **Interpretasi threshold Master/Local File**: Misi menspesifikasikan "Master+Local jika omzet grup > Rp50 M DAN transaksi afiliasi > Rp20 M". Dokumen sumber hanya menyebut kewajiban dokumen secara umum dan CbCR > Rp11 T (baris 35). Aplikasi memakai persis spesifikasi misi (kondisi AND). Catatan: PMK 172/2023 sebenarnya juga mengenal ambang terpisah untuk pinjaman (>Rp5 M) dan transaksi jasa tertentu; di luar lingkup karena dokumen sumber tidak menyebutnya.

2. **Batas strictly greater (>)**: "> Rp50 M" berarti tepat Rp50 M belum wajib (konsisten dengan bunyi "melewati threshold"). Diuji di unit test.

3. **Tahun buku = tahun kalender**: akhir tahun pajak diasumsikan 31 Desember tahun pajak (aturan umum WP Badan di Indonesia). Notifikasi CbCR jatuh tempo 12 bulan setelahnya = 31 Desember tahun berikutnya.

4. **Notifikasi CbCR = pengingat in-app**: tanpa layanan background/system notification (butuh izin sistem & itikad pada build debug), reminder diimplementasikan sebagai layar khusus yang menyimpan tahun pajak (DataStore) dan menampilkan hitung mundur + status jatuh tempo setiap widget layar dibuka. Ini memenuhi "pengingat 12 bulan setelah akhir tahun pajak" secara fungsional untuk versi offline.

5. **Checklist interaktif** disimpan di Room (kategori: pihak_berelasi, dokumen, persiapan). Tidak ada akun/sinkronisasi (offline).

6. **Verifikasi "tidak ada crash"**: tidak ada emulator di lingkungan CI; crash-free diverifikasi lewat (a) build sukses assembleDebug, (b) semua unit test hijau untuk logika inti, (c) alur UI tunggal yang sederhana. Dinyatakan eksplisit di verification.md.

7. **Icon launcher**: vector drawable tunggal (tanpa PNG/adaptive icon) agar kompatibel minSdk 24 dan tidak perlu aset raster.

8. **Bahasa**: seluruh UI, materi, dan dokumentasi dalam bahasa Indonesia (konvensi pengguna).

9. **Repo/owner**: akun GitHub `sokipman4-png`, repo `TPDocApp` dibuat publik baru (belum ada sebelumnya).

10. **Konten dokumen adalah dasar materi**: output app = ringkasan setia dokumen + struktur misi; bukan nasihat perpajakan formal. Dinyatakan sebagai disclaimer di README dan Home.