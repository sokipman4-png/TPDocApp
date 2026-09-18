package com.tpdoc.app

import com.tpdoc.app.data.ContentData
import com.tpdoc.app.ui.viewmodel.PencarianViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Logika filter/pencarian materi. */
class PencarianViewModelTest {

    @Test
    fun `query kosong menghasilkan daftar kosong`() {
        assertEquals(emptyList<ContentData.MateriItem>(), PencarianViewModel.filterMateri(ContentData.allSearchItems, "  "))
    }

    @Test
    fun `cari cbcr menemukan materi CbCR dan notifikasi`() {
        val hasil = PencarianViewModel.filterMateri(ContentData.allSearchItems, "cbcr")
        assertTrue("harus menemukan item CbCR", hasil.any { it.title.contains("CbCR", ignoreCase = true) || it.body.contains("CbCR") })
        assertTrue("harus menemukan item notifikasi", hasil.any { it.title.contains("Notifikasi", ignoreCase = true) })
    }

    @Test
    fun `cari threshold menemukan kalkulator`() {
        val hasil = PencarianViewModel.filterMateri(ContentData.allSearchItems, "threshold")
        assertTrue(hasil.any { it.section == "Kalkulator" })
    }

    @Test
    fun `cari royalti menemukan jenis transaksi royalti`() {
        val hasil = PencarianViewModel.filterMateri(ContentData.allSearchItems, "royalti")
        assertTrue(hasil.any { it.title.contains("Royalti", ignoreCase = true) })
    }

    @Test
    fun `cari 25 persen menemukan pihak berelasi`() {
        val hasil = PencarianViewModel.filterMateri(ContentData.allSearchItems, "25")
        assertTrue(hasil.any { it.section == "Pihak Berelasi" })
    }

    @Test
    fun `query tidak dikenal - hasil kosong`() {
        val hasil = PencarianViewModel.filterMateri(ContentData.allSearchItems, "xyzabc123")
        assertEquals(emptyList<ContentData.MateriItem>(), hasil)
    }

    @Test
    fun `pencarian tidak peka huruf besar kecil`() {
        val a = PencarianViewModel.filterMateri(ContentData.allSearchItems, "SEWA")
        val b = PencarianViewModel.filterMateri(ContentData.allSearchItems, "sewa")
        assertEquals(a.size, b.size)
        assertTrue(a.isNotEmpty())
    }
}