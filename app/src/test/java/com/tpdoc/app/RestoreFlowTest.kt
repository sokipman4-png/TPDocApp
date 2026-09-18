package com.tpdoc.app

import com.tpdoc.app.data.export.JsonCodec
import com.tpdoc.app.data.room.Perusahaan
import com.tpdoc.app.ui.viewmodel.RestoreFlow
import org.junit.Assert.*
import org.junit.Test

/**
 * Kontrak alur restore: memilih file backup HANYA memunculkan dialog konfirmasi
 * dengan data pending — import (replaceAll) BUKAN pernah terjadi di fase "pick",
 * hanya lewat ExportViewModel.confirmRestore() yang ditolak jika pending kosong.
 * State machine murni (RestoreFlow) tidak memiliki efek samping ke database.
 */
class RestoreFlowTest {

    private fun backupJson(p: Perusahaan): String = JsonCodec.encode(listOf(p))

    @Test
    fun `file backup valid memunculkan dialog konfirmasi dan belum mengimpor`() {
        val state = RestoreFlow.onBackupPicked(backupJson(Perusahaan(nama = "PT A", npwp = "123")))

        assertTrue("Dialog konfirmasi wajib muncul bila backup valid", state.showRestoreConfirm)
        assertEquals("Data backup wajib siap pending (1 perusahaan)", 1, state.pendingRestore.size)
        assertNull(state.statusMessage)
        assertNull(state.error)
        // Data existing belum ditimpa: tidak ada import di alur pick — import hanya di confirmRestore().
    }

    @Test
    fun `array JSON kosong ditolak tanpa dialog konfirmasi`() {
        val state = RestoreFlow.onBackupPicked("[]")

        assertFalse(state.showRestoreConfirm)
        assertNotNull("Error wajib muncul untuk backup kosong", state.error)
        assertTrue(state.pendingRestore.isEmpty())
    }

    @Test
    fun `json tidak valid ditolak tanpa dialog konfirmasi`() {
        val state = RestoreFlow.onBackupPicked("{rusak}")

        assertFalse(state.showRestoreConfirm)
        assertNotNull(state.error)
        assertTrue(state.pendingRestore.isEmpty())
    }

    @Test
    fun `file kosong ditolak`() {
        assertTrue(RestoreFlow.onBackupPicked("").error != null)
        assertTrue(RestoreFlow.onBackupPicked("   ").error != null)
    }

    @Test
    fun `data lengkap dipertahankan di pending sampai konfirmasi`() {
        val p = Perusahaan(
            id = 7,
            nama = "PT Beta",
            npwp = "999",
            alamat = "Jl. Contoh 10",
            negara = "SG",
            status = Perusahaan.STATUS_ANAK,
            parentId = 2,
            tahunPajak = 2025,
            logoPath = "/cache/logos/logo_7_1.jpg",
        )
        val state = RestoreFlow.onBackupPicked(JsonCodec.encode(listOf(p)))

        val pending = state.pendingRestore.single()
        assertEquals(7L, pending.id)
        assertEquals("PT Beta", pending.nama)
        assertEquals("999", pending.npwp)
        assertEquals("SG", pending.negara)
        assertEquals(Perusahaan.STATUS_ANAK, pending.status)
        assertEquals(2L, pending.parentId)
        assertEquals(2025, pending.tahunPajak)
        assertEquals("/cache/logos/logo_7_1.jpg", pending.logoPath)
    }
}